package ibeere.newsletter.topanswers;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.CompletableToListenableFutureAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import ibeere.aggregate.question.answer.AnswerQuestionRef;
import ibeere.user.UserId;

import java.util.Set;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Component
@RequiredArgsConstructor
class TopAnswersSendConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(TopAnswersSendConsumer.class);

    private final TopAnswersService service;

    @ServiceActivator(inputChannel = "topAnswersSendChannel", outputChannel = "ackChannel", async = "true")
    public ListenableFuture<Message> messageReceiver(Message message) {

        LOG.info("event consumed " + message.getPayload());

        return new CompletableToListenableFutureAdapter(supplyAsync(() -> {

            TopAnswersSendInput  topAnswersSendInput = new Gson().fromJson(message.getPayload().toString(), TopAnswersSendInput.class);

            service.sendTopAnswers(topAnswersSendInput.answerQuestionRefs, topAnswersSendInput.overrideUserId);

            return message;
        }));
    }

    @RequiredArgsConstructor
    static class TopAnswersSendInput {
        private final Set<AnswerQuestionRef> answerQuestionRefs;
        private final UserId overrideUserId;
    }
}