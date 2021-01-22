package ibeere.event.gateway;

import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "credentialProfileUpdatedOutputChannel")
public interface CredentialProfileUpdatedOutboundGateway {
    void sendToPubsub(String text);
}
