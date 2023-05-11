package com.example.demo.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ListenConfig {

    @Value("${pubsub.subscription.name:test-subscription}")
    private String subscriptionName;

    @Value("${pubsub.topic.name:test-topic}")
    private String topicName;

    // this is actually a configuration for the message receiver
    @Bean
    @DependsOn("messageSender")
    public PubSubInboundChannelAdapter messageChannelAdapter(
            @Qualifier("pubsubInputChannel") MessageChannel inputChannel,
            PubSubTemplate pubSubTemplate, PubSubAdmin admin) {

        // creating topic if it does not exist
        // if (admin.getTopic(topicName) == null) {
        //     admin.createTopic(topicName);
        // }

        // creating subscription if it does not exist
        if (admin.getSubscription(subscriptionName) == null) {
            admin.createSubscription(subscriptionName, topicName);
        }

        // configuring subscription
        PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, subscriptionName);
        adapter.setOutputChannel(inputChannel);

        // configuring ack mode
        adapter.setAckMode(AckMode.MANUAL);

        return adapter;
    }

    // this is practically just a needed redirector
    @Bean
    public MessageChannel pubsubInputChannel() {
        return new DirectChannel();
    }

    // TODO: this is the bean implementing the actual handling which could be
    // created as a service
    @Bean
    @ServiceActivator(inputChannel = "pubsubInputChannel")
    public MessageHandler messageReceiver() {
        return message -> {
            log.info("Message arrived! Payload: " + new String((byte[]) message.getPayload()));
            BasicAcknowledgeablePubsubMessage originalMessage = message.getHeaders()
                    .get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
            originalMessage.ack();
        };
    }
}
