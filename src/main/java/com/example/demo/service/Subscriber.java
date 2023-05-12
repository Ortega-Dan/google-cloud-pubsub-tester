package com.example.demo.service;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class Subscriber implements MessageHandler {

    private final SupposedPerformActionOnReceiveService somethingDoerBean;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {

        log.info("Message arrived! Payload: " + new String((byte[]) message.getPayload()));
        BasicAcknowledgeablePubsubMessage originalMessage = message.getHeaders()
                .get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);

        somethingDoerBean.doSomething();

        originalMessage.ack();
    }

}
