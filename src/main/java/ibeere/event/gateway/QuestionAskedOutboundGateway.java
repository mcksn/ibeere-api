package ibeere.event.gateway;

import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "questionAskedOutputChannel")
public interface QuestionAskedOutboundGateway {
    void sendToPubsub(String text);
}
