package ibeere.aggregate.question.answer.vote;

import ibeere.aggregate.question.answer.AnswerId;
import ibeere.repository.CachingJpaRepository;
import ibeere.user.UserId;

import java.util.Optional;
import java.util.UUID;

public interface UpVoteRepository extends CachingJpaRepository<UpVoteEntity, UUID> {

    Optional<UpVoteEntity> findByAnswerIdAndUserId(AnswerId answerId, UserId userId);
    long countByAnswerId(AnswerId answerId);
}