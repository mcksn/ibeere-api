package ibeere.aggregate.question;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.CompletableToListenableFutureAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import ibeere.event.EventPublisher;
import ibeere.aggregate.question.answer.AnswerQuestionRef;
import ibeere.questiondoc.QuestionDocumentService;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class QuestionAnsweredConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionAnsweredConsumer.class);

    private final QuestionDocumentService questionDocumentService;
    private final EventPublisher eventPublisher;

    @ServiceActivator(inputChannel = "questionAnsweredChannel", outputChannel = "ackChannel", async = "true")
    public ListenableFuture<Message> questionAnswered(Message message) {

        LOGGER.info("event consumed " + message.getPayload());
        return new CompletableToListenableFutureAdapter(CompletableFuture.supplyAsync(() -> {

            AnswerQuestionRef answerQuestionRef = new Gson().fromJson(message.getPayload().toString(), AnswerQuestionRef.class);

            questionDocumentService.rebuild(answerQuestionRef.questionId());
            eventPublisher.questionAnsweredAndReBuilt(answerQuestionRef);

            return message;
        }));
    }
}
