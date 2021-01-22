package ibeere.event.gateway;

import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "microProfileUpdatedOutputChannel")
public interface MicroProfileUpdatedOutboundGateway {
    void sendToPubsub(String text);
}
