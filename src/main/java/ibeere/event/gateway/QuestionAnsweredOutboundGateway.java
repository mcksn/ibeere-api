package ibeere.event.gateway;

import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "questionAnsweredOutputChannel")
public interface QuestionAnsweredOutboundGateway {
    void sendToPubsub(String text);
}
