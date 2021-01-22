package ibeere.aggregate.question;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.CompletableToListenableFutureAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import ibeere.questiondoc.QuestionDocumentService;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AllQuestionDocsUpdatedConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ibeere.aggregate.question.AllQuestionDocsUpdatedConsumer.class);

    private final QuestionDocumentService questionDocumentService;
    private final QuestionService questionService;

    @ServiceActivator(inputChannel = "allQuestionDocsUpdatedChannel", outputChannel = "ackChannel", async = "true")
    public ListenableFuture<Message> messageReceiver(Message message) {
        LOGGER.info("event consumed " + message.getPayload());

        return new CompletableToListenableFutureAdapter(CompletableFuture.supplyAsync(() -> {
            questionService.findAll().forEach(questionDocumentService::rebuild);
            return message;
        }));
    }
}