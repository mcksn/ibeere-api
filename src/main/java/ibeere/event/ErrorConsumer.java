package ibeere.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import ibeere.event.gateway.ErrorOutboundGateway;

import static org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders.ORIGINAL_MESSAGE;

@Service
public class ErrorConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(ErrorConsumer.class);
    @Autowired
    private ErrorOutboundGateway errorOutboundGateway;

    @ServiceActivator(inputChannel = "errorChannel")
    public void messageReceiver(MessagingException message) {
        LOG.error(message.toString());
        BasicAcknowledgeablePubsubMessage originalMessage =
                message.getFailedMessage().getHeaders().get(ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
        LOG.error("Message errored!  Message Id: {} original: {}", message.getFailedMessage().getHeaders().getId(), message.getFailedMessage().getPayload());
        originalMessage.ack();
        errorOutboundGateway.sendToPubsub(String.valueOf(message.getFailedMessage().getPayload()));
    }
}
