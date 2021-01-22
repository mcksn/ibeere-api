package ibeere.aggregate.question.answer.vote;

import ibeere.aggregate.question.answer.AnswerId;
import ibeere.repository.CachingJpaRepository;
import ibeere.user.UserId;

import java.util.Optional;
import java.util.UUID;

public interface DownVoteRepository extends CachingJpaRepository<DownVoteEntity, UUID> {
    long countByAnswerId(AnswerId answerId);
    Optional<DownVoteEntity> findByAnswerIdAndUserId(AnswerId answerId, UserId userId);
}