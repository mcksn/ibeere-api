package ibeere.aggregate.profile;

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
public class NewUserConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(NewUserConsumer.class);

    private final ProfileService profileService;

    @ServiceActivator(inputChannel = "newUserChannel", async = "true")
    public ListenableFuture<Message> messageReceiver(Message message) {
        LOG.info("event consumed : " + message.getPayload());
        return new CompletableToListenableFutureAdapter(CompletableFuture.supplyAsync(() -> {

            // TODO not ready for release yet, needs testing
            // requires further testing before release
            // profileService.newProfile(user);

            return message;
        }));
    }
}
