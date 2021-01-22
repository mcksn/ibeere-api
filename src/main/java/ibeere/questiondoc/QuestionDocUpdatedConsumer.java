package ibeere.questiondoc;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.CompletableToListenableFutureAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import ibeere.aggregate.question.QuestionId;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class QuestionDocUpdatedConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionDocUpdatedConsumer.class);

    private final QuestionDocumentService questionDocumentService;

    @ServiceActivator(inputChannel = "questionDocUpdatedChannel", outputChannel = "ackChannel", async = "true")
    public ListenableFuture<Message> messageReceiver(Message message) {

        LOGGER.info("event consumed " + message.getPayload());

        return new CompletableToListenableFutureAdapter(CompletableFuture.supplyAsync(() -> {
            QuestionId questionId = new Gson().fromJson(message.getPayload().toString(), QuestionId.class);

            questionDocumentService.rebuild(questionId);

            return message;
        }));

    }
}
