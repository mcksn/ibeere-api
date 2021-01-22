package ibeere.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

@Service
public class PubSubInboundChannelAdapterFactory {

    @Autowired
    private PubSubTemplate pubSubTemplate;
    @Value("${env}")
    private String env;
    @Autowired
    private MessageChannel errorChannel;

    public PubSubInboundChannelAdapter createByEnv(String name, MessageChannel inputChannel) {
        PubSubInboundChannelAdapter adapter =
                new PubSubInboundChannelAdapter(pubSubTemplate, name + "Subscription_" + env);
        adapter.setOutputChannel(inputChannel);
        adapter.setAckMode(AckMode.AUTO);
        adapter.setPayloadType(String.class);
        adapter.setErrorChannel(errorChannel);
        return adapter;
    }
}
