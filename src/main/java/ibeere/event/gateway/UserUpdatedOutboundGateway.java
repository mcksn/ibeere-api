package ibeere.event.gateway;

import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "userUpdatedOutputChannel")
public interface UserUpdatedOutboundGateway {

    void sendToPubsub(String text);

}
