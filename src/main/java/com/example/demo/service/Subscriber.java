package com.example.demo.service;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class Subscriber implements MessageHandler {

    private final SupposedPerformActionOnReceiveService somethingDoerBean;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {

        // pretty printing when comes json, also shows regular string when not json
        var payloadString = new String((byte[]) message.getPayload());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        var jsonElement = gson.fromJson(payloadString, JsonElement.class);

        // when processing notifications from google cloud storage, this gets the file
        // name
        // var fileName = jsonElement.getAsJsonObject().get("name");

        log.info("Message arrived! Payload: " + gson.toJson(jsonElement));
        BasicAcknowledgeablePubsubMessage originalMessage = message.getHeaders()
                .get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);

        somethingDoerBean.doSomething();

        originalMessage.ack();
    }

}
