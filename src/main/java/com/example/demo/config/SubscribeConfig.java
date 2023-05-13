package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import com.example.demo.service.Subscriber;
import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;

@Configuration
public class SubscribeConfig {

    @Value("${pubsub.subscription.name:test-subscription}")
    private String subscriptionName;

    @Value("${pubsub.topic.name:test-topic}")
    private String topicName;

    @Value("${pubsub.createtopicandsubs:false}")
    private boolean createtopicandsubs;

    @Autowired
    private Subscriber subscriber;

    private static final String INPUT_CHANNEL = "inputChannel";

    // the spring message channel
    @Bean
    public MessageChannel inputChannel() {
        return new DirectChannel();
    }

    // this is actually a configuration for the message receiver
    // @DependsOn("messageSender")
    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapter(
            @Qualifier(INPUT_CHANNEL) MessageChannel inputChannel,
            PubSubTemplate pubSubTemplate, PubSubAdmin admin) {

        // creating topic and subscription if they do not exist
        if (createtopicandsubs) {
            // creating topic if it does not exist
            if (admin.getTopic(topicName) == null) {
                admin.createTopic(topicName);
            }

            // creating subscription if it does not exist
            if (admin.getSubscription(subscriptionName) == null) {
                admin.createSubscription(subscriptionName, topicName);
            }
        }

        // configuring subscription
        PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, subscriptionName);
        // sending to a spring message channel
        adapter.setOutputChannel(inputChannel);

        // configuring ack mode
        adapter.setAckMode(AckMode.MANUAL);

        return adapter;
    }

    // this activates the bean implementing the actual message-receive handling
    @Bean
    @ServiceActivator(inputChannel = INPUT_CHANNEL)
    public MessageHandler messageReceiver() {
        return subscriber;
    }
}
