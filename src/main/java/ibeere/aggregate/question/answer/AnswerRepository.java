package ibeere.aggregate.question.answer;

import org.springframework.data.jpa.repository.Query;
import ibeere.aggregate.question.QuestionId;
import ibeere.repository.CachingJpaRepository;
import ibeere.user.UserId;

import java.util.List;

public interface AnswerRepository extends CachingJpaRepository<AnswerEntity, AnswerId> {

    @Query("SELECT new ibeere.aggregate.question.answer.AnswerQuestionRef(a.id, a.questionId) FROM AnswerEntity a where a.userId = :userId")
    List<AnswerQuestionRef> findIdByUserId(UserId userId);
    List<AnswerEntity> findByQuestionId(QuestionId questionId);
}