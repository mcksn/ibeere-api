package ibeere.aggregate.question.follow;

import ibeere.aggregate.question.QuestionId;
import ibeere.repository.CachingJpaRepository;
import ibeere.user.UserId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FollowRepository extends CachingJpaRepository<FollowEntity, UUID> {

    long countByQuestionId(QuestionId questionId);
    Optional<FollowEntity> findByQuestionIdAndUserId(QuestionId questionId, UserId userId);
    List<FollowEntity> findByUserId(UserId userId);
    Optional<FollowEntity> findFirstByQuestionIdOrderBySubmitDateDesc(QuestionId questionId);
    List<FollowEntity> findByQuestionId(QuestionId questionId);

}