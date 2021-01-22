package ibeere.user;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.CompletableToListenableFutureAdapter;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserUpdatedConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(UserUpdatedConsumer.class);

    private final UserDocumentService userDocumentService;

    @ServiceActivator(inputChannel = "userUpdatedChannel", outputChannel = "ackChannel", async = "true")
    public ListenableFuture<Message> messageReceiver(Message message) {
        LOG.info("event consumed " + message.getPayload());

        return new CompletableToListenableFutureAdapter(CompletableFuture.supplyAsync(() -> {
            UserId userId = new Gson().fromJson(message.getPayload().toString(), UserId.class);
            userDocumentService.rebuild(userId);
            return message;
        }));
    }
}
