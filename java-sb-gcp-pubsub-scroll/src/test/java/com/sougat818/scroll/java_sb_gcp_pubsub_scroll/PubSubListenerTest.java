package com.sougat818.scroll.java_sb_gcp_pubsub_scroll;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.Topic;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.TimeUnit;
import com.google.api.gax.core.NoCredentialsProvider;

@ExtendWith(MockitoExtension.class)
public class PubSubListenerTest {
    private static final String PROJECT_ID = "test-project";
    private static final String TOPIC_ID = "test-topic";
    private static final String SUBSCRIPTION_ID = "test-subscription";
    private static final Logger log = LoggerFactory.getLogger(PubSubListenerTest.class);
    private static GenericContainer<?> pubsubEmulator;
    private static String emulatorHostPort;

    @Spy
    private final ObjectMapper objectMapper= new ObjectMapper();

    @BeforeAll
    static void setUp() throws Exception {
        pubsubEmulator = new GenericContainer<>(DockerImageName.parse("storytel/gcp-pubsub-emulator:latest"))
                .withExposedPorts(8085)
                .waitingFor(Wait.forListeningPort())
                .withCommand("gcloud", "beta", "emulators", "pubsub", "start", "--host-port=0.0.0.0:8085");
        pubsubEmulator.start();
        emulatorHostPort = "127.0.0.1:" + pubsubEmulator.getMappedPort(8085);
        System.setProperty("PUBSUB_EMULATOR_HOST", emulatorHostPort);

        // Create topic and subscription
        TopicAdminSettings topicAdminSettings = TopicAdminSettings.newBuilder()
                .setEndpoint(emulatorHostPort)
                .setTransportChannelProvider(TopicAdminSettings.defaultTransportChannelProvider())
                .setCredentialsProvider(NoCredentialsProvider.create())
                .build();
        try (TopicAdminClient topicAdminClient = TopicAdminClient.create(topicAdminSettings)) {
            Topic topic = topicAdminClient.createTopic(ProjectTopicName.of(PROJECT_ID, TOPIC_ID));
        }
        SubscriptionAdminSettings subscriptionAdminSettings = SubscriptionAdminSettings.newBuilder()
                .setEndpoint(emulatorHostPort)
                .setCredentialsProvider(NoCredentialsProvider.create())
                .build();
        try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create(subscriptionAdminSettings)) {
            Subscription subscription = subscriptionAdminClient.createSubscription(
                    ProjectSubscriptionName.of(PROJECT_ID, SUBSCRIPTION_ID),
                    ProjectTopicName.of(PROJECT_ID, TOPIC_ID),
                    PushConfig.getDefaultInstance(),
                    10);
        }
    }

    @AfterAll
    static void tearDown() {
        if (pubsubEmulator != null) {
            pubsubEmulator.stop();
        }
    }

    @Test
    void testListenerReceivesValidMessage() throws Exception {
        PubSubListener listener = new PubSubListener(objectMapper);
        listener.listen(PROJECT_ID, SUBSCRIPTION_ID);
        ObjectMapper objectMapper = new ObjectMapper();
        String validJson = objectMapper.writeValueAsString(new MyMessage("1", "hello"));
        String invalidJson = "{\"foo\":\"bar\"}";

        Publisher publisher = Publisher.newBuilder(ProjectTopicName.of(PROJECT_ID, TOPIC_ID))
                .setEndpoint(emulatorHostPort)
                .setCredentialsProvider(NoCredentialsProvider.create())
                .build();
        // Publish valid message
        publisher.publish(PubsubMessage.newBuilder().setData(com.google.protobuf.ByteString.copyFromUtf8(validJson)).build()).get();
        // Publish invalid message
        publisher.publish(PubsubMessage.newBuilder().setData(com.google.protobuf.ByteString.copyFromUtf8(invalidJson)).build()).get();
        publisher.shutdown();
        publisher.awaitTermination(5, TimeUnit.SECONDS);

        // Wait for listener to process
        TimeUnit.SECONDS.sleep(2);
        listener.stopListening();

        Mockito.verify(objectMapper,Mockito.times(1)).readValue(validJson, MyMessage.class);
    }
} 