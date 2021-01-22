package ibeere.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import ibeere.event.ConsumeOnceMessageHandlerFactory;
import ibeere.event.MessageHandlerFactory;
import ibeere.event.PubSubInboundChannelAdapterFactory;

@Configuration
public class EventConfig {

    @Autowired
    private PubSubInboundChannelAdapterFactory pubSubInboundChannelAdapterFactory;

    @Autowired
    private MessageHandlerFactory messageHandlerFactory;

    @Autowired
    private ConsumeOnceMessageHandlerFactory sharedMessageHandlerFactory;

    @Bean
    public MessageChannel userUpdatedChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "userUpdatedOutputChannel")
    public MessageHandler userUpdatedMessageSender() {
        return sharedMessageHandlerFactory.create("userUpdated");
    }
    @Bean
    public PubSubInboundChannelAdapter userUpdatedChannelAdapter(
            @Qualifier("userUpdatedChannel") MessageChannel inputChannel,
            PubSubTemplate pubSubTemplate) {
        return pubSubInboundChannelAdapterFactory.createByEnv("userUpdated", inputChannel);
    }

    // micro profile updated

    @Bean
    @ServiceActivator(inputChannel = "microProfileUpdatedOutputChannel")
    public MessageHandler microProfileUpdatedMessageSender() {
        return sharedMessageHandlerFactory.create("microProfileUpdated");
    }
    @Bean
    public PubSubInboundChannelAdapter microProfileUpdatedAdapter(
            @Qualifier("microProfileUpdatedChannel") MessageChannel inputChannel) {
        return pubSubInboundChannelAdapterFactory.createByEnv("microProfileUpdated", inputChannel);
    }
    @Bean
    public MessageChannel microProfileUpdatedChannel() {
        return new DirectChannel();
    }

    // credential profile updated

    @Bean
    @ServiceActivator(inputChannel = "credentialProfileUpdatedOutputChannel")
    public MessageHandler credentialProfileUpdatedMessageSender() {
        return sharedMessageHandlerFactory.create("credentialProfileUpdated");
    }
    @Bean
    public PubSubInboundChannelAdapter credentialProfileUpdatedAdapter(
            @Qualifier("credentialProfileUpdatedChannel") MessageChannel inputChannel) {
        return pubSubInboundChannelAdapterFactory.createByEnv("credentialProfileUpdated", inputChannel);
    }
    @Bean
    public MessageChannel credentialProfileUpdatedChannel() {
        return new DirectChannel();
    }

    // question updated

    @Bean
    @ServiceActivator(inputChannel = "questionDocUpdatedOutputChannel")
    public MessageHandler questionDocUpdatedMessageSender() {
        return sharedMessageHandlerFactory.create("questionDocUpdated");
    }
    @Bean
    public PubSubInboundChannelAdapter questionDocUpdatedAdapter(
            @Qualifier("questionDocUpdatedChannel") MessageChannel inputChannel) {
        return pubSubInboundChannelAdapterFactory.createByEnv("questionDocUpdated", inputChannel);
    }
    @Bean
    public MessageChannel questionDocUpdatedChannel() {
        return new DirectChannel();
    }

    // question answered

    @Bean
    public PubSubInboundChannelAdapter questionAnsweredAdapter(
            @Qualifier("questionAnsweredChannel") MessageChannel inputChannel) {
        return pubSubInboundChannelAdapterFactory.createByEnv("questionAnswered", inputChannel);
    }
    @Bean
    public MessageChannel questionAnsweredChannel() {
        return new DirectChannel();
    }
    @Bean
    @ServiceActivator(inputChannel = "questionAnsweredOutputChannel")
    public MessageHandler questionAnsweredMessageSender(PubSubTemplate pubsubTemplate) {
        return messageHandlerFactory.createByEnv("questionAnswered");
    }

    //question answered rebuilt

    @Bean
    public PubSubInboundChannelAdapter questionAnsweredRebuiltAdapter(
            @Qualifier("questionAnsweredRebuiltChannel") MessageChannel inputChannel) {
        return pubSubInboundChannelAdapterFactory.createByEnv("questionAnsweredRebuilt", inputChannel);
    }
    @Bean
    public MessageChannel questionAnsweredRebuiltChannel() {
        return new DirectChannel();
    }
    @Bean
    @ServiceActivator(inputChannel = "questionAnsweredRebuiltOutputChannel")
    public MessageHandler questionAnsweredRebuiltMessageSender() {
        return messageHandlerFactory.createByEnv("questionAnsweredRebuilt");
    }

    @Bean
    public PubSubInboundChannelAdapter questionAskedAdapter(
            @Qualifier("questionAskedChannel") MessageChannel inputChannel) {
        return pubSubInboundChannelAdapterFactory.createByEnv("questionAsked", inputChannel);
    }
    @Bean
    public MessageChannel questionAskedChannel() {
        return new DirectChannel();
    }
    @Bean
    @ServiceActivator(inputChannel = "questionAskedOutputChannel")
    public MessageHandler questionAskedMessageSender(PubSubTemplate pubsubTemplate) {
        return messageHandlerFactory.createByEnv("questionAsked");
    }

    // top answers send

    @Bean
    public PubSubInboundChannelAdapter topAnswersSendAdapter(
            @Qualifier("topAnswersSendChannel") MessageChannel inputChannel) {
        return pubSubInboundChannelAdapterFactory.createByEnv("topAnswersSend", inputChannel);
    }
    @Bean
    public MessageChannel topAnswersSendChannel() {
        return new DirectChannel();
    }

    // all question docs updated

    @Bean
    public PubSubInboundChannelAdapter allQuestionDocsUpdatedAdapter(
            @Qualifier("allQuestionDocsUpdatedChannel") MessageChannel inputChannel) {
        return pubSubInboundChannelAdapterFactory.createByEnv("allQuestionDocsUpdated", inputChannel);
    }
    @Bean
    public MessageChannel allQuestionDocsUpdatedChannel() {
        return new DirectChannel();
    }

    // new user

    @Bean
    public MessageChannel newUserChannel() {
        return new DirectChannel();
    }
    @Bean
    public PubSubInboundChannelAdapter newUserAdapter(MessageChannel newUserChannel) {
        return pubSubInboundChannelAdapterFactory.createByEnv("newUser", newUserChannel);
    }
    @Bean
    @ServiceActivator(inputChannel = "newUserOutputChannel")
    public MessageHandler newUserMessageSender() {
        return messageHandlerFactory.createByEnv("newUser");
    }

    // error

    @Bean
    public MessageChannel errorChannel() {
        return new DirectChannel();
    }
    @Bean
    @ServiceActivator(inputChannel = "errorOutputChannel")
    public MessageHandler errorMessageSender() {
        return messageHandlerFactory.createByEnv("error");
    }

    // ack

    @Bean
    public MessageChannel ackChannel() {
        return new DirectChannel();
    }

    // notes

    // service actor output channel needs a return type on the service activator to send a reply to output channel
    // https://stackoverflow.com/questions/46804624/spring-integration-service-activator-without-output-channel
}
