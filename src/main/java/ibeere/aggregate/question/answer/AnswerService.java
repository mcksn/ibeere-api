package ibeere.aggregate.question.answer;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ibeere.aggregate.question.answer.vote.*;
import ibeere.event.EventPublisher;
import ibeere.support.ClockProvider;
import ibeere.user.UserId;
import ibeere.user.UserInputException;

import java.util.*;

import static java.time.Instant.now;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;

@Service
@Transactional
@RequiredArgsConstructor
public class AnswerService {
    private static final Logger LOG = LoggerFactory.getLogger(AnswerService.class);

    private final AnswerRepository answerRepository;
    private final JdbcTemplate jdbcTemplate;
    private final UpVoteRepository upVoteRepository;
    private final DownVoteRepository downVoteRepository;
    private final EventPublisher eventPublisher;
    private final ClockProvider clockProvider;

    public boolean upVotedByAsker(UserId askerId, AnswerId answerId) {
        return upVoteRepository.findByAnswerIdAndUserId(answerId, askerId).isPresent();
    }

    public boolean downVotedByAsker(UserId askerId, AnswerId answerId) {
        return downVoteRepository.findByAnswerIdAndUserId(answerId, askerId).isPresent();
    }

    public Votes voteAggregationCount(AnswerId answerId) {
        final long downVotesCount = downVoteRepository.countByAnswerId(answerId);
        final long upVotesCount = upVoteRepository.countByAnswerId(answerId);

        long upMinusDownWithFloor = Math.max(0, upVotesCount - downVotesCount);
        LOG.info(answerId + " - vote agg - " + upMinusDownWithFloor);
        return new Votes(downVotesCount, upVotesCount, upMinusDownWithFloor);
    }

    @Transactional
    public boolean cancelUpVote(UserId userId, AnswerQuestionRef answerQuestionRef) {

        this.upVoteRepository.findByAnswerIdAndUserId(answerQuestionRef.answerId(), userId)
                .filter(d -> d.getAnswerId().equals(answerQuestionRef.answerId()))
                .ifPresent(entity -> {
                    this.upVoteRepository.delete(entity);
                    // TODO move to listener in the entity so this is only sent for successful transactions
                    eventPublisher.upVoteCancelled(answerQuestionRef, userId);
                });
        return true;
    }

    @Transactional
    public boolean cancelDownVote(UserId userId, AnswerQuestionRef answerQuestionRef) {

        downVoteRepository.findByAnswerIdAndUserId(answerQuestionRef.answerId(), userId)
                .filter(d -> d.getAnswerId().equals(answerQuestionRef.answerId()))
                .ifPresent(entity -> {
                    downVoteRepository.delete(entity);
                    // TODO move to listener in the entity so this is only sent for successful transactions
                    eventPublisher.downVoteCancelled(answerQuestionRef, userId);
                });
        return true;
    }


    @Transactional
    public boolean upVote(UserId userId, AnswerQuestionRef answerQuestionRef) throws UserInputException {

        if (upVoteRepository.findByAnswerIdAndUserId(answerQuestionRef.answerId(), userId).isPresent()) {
            LOG.error("Attempting to upVote when already upVoted. " + answerQuestionRef.answerId() + " " + userId);
            throw new UserInputException("Attempting to upVote when already upVoted.");
        }

        upVoteRepository.save(new UpVoteEntity(
                randomUUID(),
                userId,
                answerQuestionRef.answerId(),
                now(clockProvider.standardClock())));

        downVoteRepository.findByAnswerIdAndUserId(answerQuestionRef.answerId(), userId)
                .filter(downVoteEntity -> downVoteEntity.getAnswerId().equals(answerQuestionRef.answerId()))
                .ifPresent(this.downVoteRepository::delete);

        // TODO move to listener in the entity so this is only sent for successful transactions
        eventPublisher.answerUpVoted(answerQuestionRef, userId);
        return true;
    }

    @Transactional
    public boolean downVote(UserId userId, AnswerQuestionRef answerQuestionRef) throws UserInputException {
        if (downVoteRepository.findByAnswerIdAndUserId(answerQuestionRef.answerId(), userId).isPresent()) {
            LOG.error("Attempting to downVote when already downVoted. " + answerQuestionRef.answerId() + " " + userId);
            throw new UserInputException("Attempting to downVote when already downVoted.");
        }

        downVoteRepository.save(new DownVoteEntity(
                randomUUID(),
                userId,
                answerQuestionRef.answerId(),
                now(clockProvider.standardClock())));

        upVoteRepository.findByAnswerIdAndUserId(answerQuestionRef.answerId(), userId)
                .filter(downVoteEntity -> downVoteEntity.getAnswerId().equals(answerQuestionRef.answerId()))
                .ifPresent(this.upVoteRepository::delete);

        // TODO move to listener in the entity so this is only sent for successful transactions
        eventPublisher.answerDownVoted(answerQuestionRef, userId);
        return true;

    }

    @Transactional
    public boolean updateAnswerContent(AnswerQuestionRef answerQuestionRef, String editorState, UserId userId) {
        answerRepository.findById(answerQuestionRef.answerId())
                .ifPresent(answer -> {
                    answer.updateContent(editorState);
                    answerRepository.save(answer);
                });

        return true;
    }

    public Optional<AnswerEntity> findAnswerContent(AnswerQuestionRef answerQuestionRef, UserId userId) {
        return answerRepository.findById(answerQuestionRef.answerId());
    }

    @Transactional
    public boolean updateAnswerViewCounts(Map<AnswerId, Long> answerViewCounts) {
        LOG.info("Attempting to update " + answerViewCounts.size() + " answer view counts");

        List<Object[]> batch = answerViewCounts.entrySet().stream()
                .map(entry -> new Object[]{entry.getValue(), entry.getKey().getId(), entry.getValue()})
                .collect(toList());

        int[] updateCounts = jdbcTemplate.batchUpdate(
                "update answer_entity set view_count =  ? where answer_id = ? and ? > view_count",
                batch);
        LOG.info("Updated " + updateCounts.length + " answer view counts");

        return true;
    }

    public List<AnswerQuestionRef> findPublishedAnswersByDeviceId(UserId userId) {
        return this.answerRepository.findIdByUserId(userId);
    }
}
