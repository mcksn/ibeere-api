package ibeere.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.outbound.PubSubMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Service;

@Service
public class MessageHandlerFactory {

    @Autowired
    private PubSubTemplate pubSubTemplate;
    @Value("${env}")
    private String env;
    @Qualifier("errorChannel")
    private MessageChannel errorChannel;

    public MessageHandler createByEnv(String name) {
        return new PubSubMessageHandler(pubSubTemplate, name+ "Topic_" + env);
    }
}
