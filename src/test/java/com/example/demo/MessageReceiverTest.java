package com.example.demo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;

import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MessageReceiverTest {

    // create logger
    private static final Logger log = LoggerFactory.getLogger(MessageReceiverTest.class);

    
    @Autowired
    private MessageHandler messageReceiver;
    
    @Test
    public void testMessageReceiver() {
        // Mock the necessary objects
        BasicAcknowledgeablePubsubMessage originalMessage = mock(BasicAcknowledgeablePubsubMessage.class);
        Message<byte[]> message = MessageBuilder.withPayload("Test Message".getBytes())
        .setHeader(GcpPubSubHeaders.ORIGINAL_MESSAGE, originalMessage)
        .build();
        
        // Call the message receiver
        messageReceiver.handleMessage(message);
        
        log.info("It works!");
        
        // Verify the expected behavior
        verify(originalMessage, times(1)).ack();
    }
}
