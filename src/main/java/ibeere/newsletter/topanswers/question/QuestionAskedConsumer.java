package ibeere.newsletter.topanswers.question;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.CompletableToListenableFutureAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import ibeere.newsletter.newquestion.question.Question;
import ibeere.newsletter.newquestion.question.QuestionRepository;
import ibeere.aggregate.question.QuestionId;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/** Consumes question asked events in a 'top answers newsletter' context */
@Service("topanswers.QuestionAskedConsumer")
@RequiredArgsConstructor
class QuestionAskedConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(QuestionAskedConsumer.class);

    private final QuestionRepository questionRepository;

    @ServiceActivator(inputChannel = "questionAskedChannel", outputChannel = "ackChannel", async = "true")
    public ListenableFuture<Message> messageReceiver(Message message) {

        LOG.info("event consumed " + message.getPayload());

        return new CompletableToListenableFutureAdapter(supplyAsync(() -> {

            QuestionId questionId = new Gson().fromJson(message.getPayload().toString(), QuestionId.class);

            questionRepository.save(new Question(questionId));

            return message;
        }));
    }
}