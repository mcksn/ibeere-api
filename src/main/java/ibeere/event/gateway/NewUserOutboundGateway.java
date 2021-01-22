package ibeere.event.gateway;

import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "newUserOutputChannel")
public interface NewUserOutboundGateway {
    void sendToPubsub(String text);
}
