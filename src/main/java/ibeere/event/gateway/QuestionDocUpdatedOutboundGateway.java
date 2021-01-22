package ibeere.event.gateway;

import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "questionDocUpdatedOutputChannel")
public interface QuestionDocUpdatedOutboundGateway {
    void sendToPubsub(String text);

}
