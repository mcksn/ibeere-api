package ibeere.aggregate.question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import ibeere.page.relevantpage.RelevantQuestion;
import ibeere.repository.CachingJpaRepository;
import ibeere.user.UserId;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends CachingJpaRepository<QuestionEntity, QuestionId> {

    @Query(value = "select cast(question_id as varchar) questionId, ts_rank_cd(to_tsvector('english', question_text), query) as rank\n" +
            "      from question_entity,\n" +
            "           to_tsquery('english',\n" +
            "                      replace(:path, '-', ' | ')) query\n" +
            "      where query @@ to_tsvector('english', question_text) and path != :path \n" +
            "      order by rank desc\n" +
            "      limit 3", nativeQuery = true)
    List<RelevantQuestion> findRelevant(String path);

    @Query("SELECT q.id FROM QuestionEntity q where q.path = :path")
    Optional<QuestionId> findIdByPath(String path);

    @Query("SELECT q.id FROM QuestionEntity q where q.path in :paths")
    List<QuestionId> findIdsByPaths(List<String> paths);

    @Query("SELECT q.id FROM QuestionEntity q where q.userId = :userId")
    List<QuestionId> findIdByUserId(UserId userId);

    @Query("SELECT q.id FROM QuestionEntity q where q.qandAUserId = :userId")
    List<QuestionId> findIdByQandAUserId(UserId userId);

    @Query("SELECT q.id FROM QuestionEntity q order by q.submitDate desc")
    Page<QuestionId> findIdsAll(Pageable pageable);
}
