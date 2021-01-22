package ibeere.aggregate.profile.micro;

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
public class MicroProfileUpdatedConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MicroProfileUpdatedConsumer.class);

    private final CachedMicroProfileService cachedMicroProfileService;

    @ServiceActivator(inputChannel = "microProfileUpdatedChannel", outputChannel = "ackChannel", async = "true")
    public ListenableFuture<Message> messageReceiver(Message message) {

        LOGGER.info("event consumed " + message.getPayload());

        return new CompletableToListenableFutureAdapter(CompletableFuture.supplyAsync(() -> {
            UserId userId = new Gson().fromJson(message.getPayload().toString(), UserId.class);

            cachedMicroProfileService.rebuild(userId);

            return message;
        }));

    }
}
