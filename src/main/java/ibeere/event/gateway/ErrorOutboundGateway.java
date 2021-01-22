package ibeere.event.gateway;

import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "errorOutputChannel")
public interface ErrorOutboundGateway {
    void sendToPubsub(String text);
}
