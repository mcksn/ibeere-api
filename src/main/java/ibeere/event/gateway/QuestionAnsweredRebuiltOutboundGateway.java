package ibeere.event.gateway;

import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "questionAnsweredRebuiltOutputChannel")
public interface QuestionAnsweredRebuiltOutboundGateway {
    void sendToPubsub(String text);
}
