package ibeere.aggregate.credential.profile;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.CompletableToListenableFutureAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import ibeere.user.UserId;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class CredentialProfileUpdatedConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(CredentialProfileUpdatedConsumer.class);

    private final CachedCredentialProfileService cachedCredentialProfileService;

    @ServiceActivator(inputChannel = "credentialProfileUpdatedChannel", outputChannel = "ackChannel", async = "true")
    public ListenableFuture<Message> messageReceiver(Message message) {
        LOG.info("event consumed " + message.getPayload());

        return new CompletableToListenableFutureAdapter(CompletableFuture.supplyAsync(() -> {
            UserId userId = new Gson().fromJson(message.getPayload().toString(), UserId.class);

            cachedCredentialProfileService.rebuild(userId);

            return message;
        }));

    }
}
