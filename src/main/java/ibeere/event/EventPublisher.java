package ibeere.event;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ibeere.aggregate.question.QuestionId;
import ibeere.aggregate.question.answer.AnswerQuestionRef;
import ibeere.event.gateway.*;
import ibeere.user.User;
import ibeere.user.UserId;

@Service
@RequiredArgsConstructor
public class EventPublisher {
    private static final Logger LOG = LoggerFactory.getLogger(EventPublisher.class);

    private final UserUpdatedOutboundGateway userUpdatedOutboundGateway;
    private final QuestionDocUpdatedOutboundGateway questionDocUpdatedOutboundGateway;
    private final QuestionAnsweredRebuiltOutboundGateway questionAnsweredRebuiltOutboundGateway;
    private final QuestionAnsweredOutboundGateway questionAnsweredOutboundGateway;
    private final MicroProfileUpdatedOutboundGateway microProfileUpdatedOutboundGateway;
    private final CredentialProfileUpdatedOutboundGateway credentialProfileUpdatedOutboundGateway;
    private final QuestionAskedOutboundGateway questionAskedOutboundGateway;
    private final NewUserOutboundGateway newUserOutboundGateway;

    public void answerUpVoted(AnswerQuestionRef answerQuestionRef, UserId userId) {
        userUpdatedOutboundGateway.sendToPubsub(new Gson().toJson(userId));
        LOG.info("userUpdated event published" + " " + userId);
        questionDocUpdatedOutboundGateway.sendToPubsub(new Gson().toJson(answerQuestionRef.questionId()));
        LOG.info("questionDocUpdated event published" + " " + answerQuestionRef.questionId());
    }

    public void upVoteCancelled(AnswerQuestionRef answerQuestionRef, UserId userId) {
        userUpdatedOutboundGateway.sendToPubsub(new Gson().toJson(userId));
        LOG.info("userUpdated event published" + " " + userId);
        questionDocUpdatedOutboundGateway.sendToPubsub(new Gson().toJson(answerQuestionRef.questionId()));
        LOG.info("questionDocUpdated event published" + " " + answerQuestionRef.questionId());
    }

    public void answerDownVoted(AnswerQuestionRef answerQuestionRef, UserId userId) {
        userUpdatedOutboundGateway.sendToPubsub(new Gson().toJson(userId));
        LOG.info("userUpdated event published" + " " + userId);
        questionDocUpdatedOutboundGateway.sendToPubsub(new Gson().toJson(answerQuestionRef.questionId()));
        LOG.info("questionDocUpdated event published" + " " + answerQuestionRef.questionId());
    }

    public void downVoteCancelled(AnswerQuestionRef answerQuestionRef, UserId userId) {
        userUpdatedOutboundGateway.sendToPubsub(new Gson().toJson(userId));
        LOG.info("userUpdated event published" + " " + userId);
        questionDocUpdatedOutboundGateway.sendToPubsub(new Gson().toJson(answerQuestionRef.questionId()));
        LOG.info("questionDocUpdated event published" + " " + answerQuestionRef.questionId());
    }

    public void questionFollowed(QuestionId questionId, UserId userId) {
        userUpdatedOutboundGateway.sendToPubsub(new Gson().toJson(userId));
        LOG.info("userUpdated event published" + " " + userId);
        questionDocUpdatedOutboundGateway.sendToPubsub(new Gson().toJson(questionId));
        LOG.info("questionDocUpdated event published" + " " + questionId);
    }

    public void questionUnfollowed(QuestionId questionId, UserId userId) {
        userUpdatedOutboundGateway.sendToPubsub(new Gson().toJson(userId));
        LOG.info("userUpdated event published" + " " + userId);
        questionDocUpdatedOutboundGateway.sendToPubsub(new Gson().toJson(questionId));
        LOG.info("questionDocUpdated event published" + " " + questionId);
    }

    public void questionDocUpdated(QuestionId questionId) {
        questionDocUpdatedOutboundGateway.sendToPubsub(new Gson().toJson(questionId));
        LOG.info("questionDocUpdated event published" + " " + questionId);
    }

    public void questionAnswered(AnswerQuestionRef answerQuestionRef) {
        questionAnsweredOutboundGateway.sendToPubsub(new Gson().toJson(answerQuestionRef));
        LOG.info("questionAnswered event published" + " " + answerQuestionRef);
    }

    public void microProfileUpdated(UserId userId) {
        microProfileUpdatedOutboundGateway.sendToPubsub(new Gson().toJson(userId));
        LOG.info("micro profile updated event published" + " " + userId);
    }

    public void credentialProfileUpdated(UserId userId) {
        credentialProfileUpdatedOutboundGateway.sendToPubsub(new Gson().toJson(userId));
        LOG.info("credential profile updated event published" + " " + userId);
    }

    public void questionAsked(QuestionId questionId) {
        questionAskedOutboundGateway.sendToPubsub(new Gson().toJson(questionId));
        LOG.info("questionAsked event published" + " " + questionId);
    }

    public void questionAnsweredAndReBuilt(AnswerQuestionRef answerQuestionRef) {
        questionAnsweredRebuiltOutboundGateway.sendToPubsub(new Gson().toJson(answerQuestionRef));
        LOG.info("questionAnsweredRebuilt event published" + " " + answerQuestionRef);
    }

    public void newUser(User user) {
        newUserOutboundGateway.sendToPubsub(new Gson().toJson(user));
        LOG.info("newUser event published" + " " + user.getId());
    }
}
