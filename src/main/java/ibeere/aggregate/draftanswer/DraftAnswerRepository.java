package ibeere.aggregate.draftanswer;

import ibeere.aggregate.question.QuestionId;
import ibeere.aggregate.question.answer.AnswerId;
import ibeere.repository.CachingJpaRepository;
import ibeere.user.UserId;

import java.util.List;
import java.util.Optional;

public interface DraftAnswerRepository extends CachingJpaRepository<DraftAnswerEntity, AnswerId> {

    Optional<DraftAnswerEntity> findByQuestionIdAndUserId(QuestionId question, UserId userId);
    List<DraftAnswerEntity> findByUserId(UserId userId);
}