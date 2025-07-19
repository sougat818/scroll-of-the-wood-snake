package com.sougat818.scroll.java_sb_gcp_pubsub_scroll;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
public class PubSubListener {
    private final ObjectMapper objectMapper;
    @Getter
    private Subscriber subscriber;

    @PostConstruct
    public void startListening() {
        listen("test", "test");
    }

    public void listen(String projectId, String subscriptionId) {
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);
        subscriber = Subscriber.newBuilder(subscriptionName, (MessageReceiver) (message, consumer) -> {
            try {
                MyMessage myMessage = objectMapper.readValue(message.getData().toStringUtf8(), MyMessage.class);
                log.info("Received valid MyMessage: {}", myMessage);
            } catch (Exception e) {
                log.warn("Received invalid message: {}", message.getData().toStringUtf8());
            } finally {
                consumer.ack();
            }
        }).build();
        subscriber.startAsync().awaitRunning();
    }

    public void stopListening() {
        if (subscriber != null) {
            subscriber.stopAsync();
        }
    }

}