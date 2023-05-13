package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;

import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;

@Configuration
public class PublishConfig {

    @Value("${pubsub.topic.name:test-topic}")
    private String topicName;

    @Value("${pubsub.createtopicandsubs:false}")
    private boolean createtopicandsubs;

    private static final String OUTPUT_CHANNEL = "outputChannel";

    // this is again the configuration bean
    @Bean
    @ServiceActivator(inputChannel = OUTPUT_CHANNEL)
    public MessageHandler messageSender(PubSubTemplate pubsubTemplate, PubSubAdmin admin) {

        // creating topic if it does not exist
        if (createtopicandsubs) {
            if (admin.getTopic(topicName) == null) {
                admin.createTopic(topicName);
            }
        }

        // configuring topic
        return new PubSubMessageHandler(pubsubTemplate, topicName);
    }

    // this produces a bean of an arbitrary type (PubSubPublisher in this case)
    // which is the one we can
    // inject elsewhere and use to publish messages
    @MessagingGateway(defaultRequestChannel = OUTPUT_CHANNEL)
    public interface Publisher {
        void publish(String text);
    }

}
