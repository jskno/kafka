package com.jskno.productsapp.service;

import com.jskno.kafka.event.driven.ProductCreatedEvent;
import com.jskno.productsapp.domain.CreateProductRestModel;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"
)
@EmbeddedKafka(partitions = 3, count = 3, controlledShutdown = true)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
public class ProductServiceV4SyncIntegrationTest {

    @Autowired
    private ProductServiceV4Sync productServiceV4Sync;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private Environment env;

    private KafkaMessageListenerContainer<String, ProductCreatedEvent> container;

    private BlockingQueue<ConsumerRecord<String, ProductCreatedEvent>> records;

    @BeforeAll
    void setUp() {
        DefaultKafkaConsumerFactory<String, Object> consumerFactory = new DefaultKafkaConsumerFactory<>(getConsumerProperties());

        ContainerProperties containerProperties = new ContainerProperties(env.getProperty("product-created-events-topic-name"));
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, ProductCreatedEvent>) records::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @AfterAll
    void tearDown() {
        container.stop();
    }

    @Test
    void testCreateProduct_whenGivenValidProductDetails_successfulSendsKafkaMessage() throws InterruptedException {
        CreateProductRestModel productRestModel = CreateProductRestModel.builder()
                .title("RestFul for Dummies")
                .quantity(1)
                .price(new BigDecimal("23.55"))
                .build();

        String productId = productServiceV4Sync.createProduct(productRestModel);

        Assertions.assertNotNull(productId);
        ConsumerRecord<String, ProductCreatedEvent> message = records.poll(3000, TimeUnit.MILLISECONDS);
        Assertions.assertNotNull(message);
        Assertions.assertNotNull(message.key());
        Assertions.assertEquals(productId, message.key());
        ProductCreatedEvent productCreatedEvent = message.value();
        Assertions.assertNotNull(productCreatedEvent);
        Assertions.assertEquals(productRestModel.getTitle(), productCreatedEvent.getTitle());
        Assertions.assertEquals(productRestModel.getQuantity(), productCreatedEvent.getQuantity());
        Assertions.assertEquals(productRestModel.getPrice(), productCreatedEvent.getPrice());
    }

    private Map<String, Object> getConsumerProperties() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString(),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
                ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class,
                ConsumerConfig.GROUP_ID_CONFIG, Objects.requireNonNull(env.getProperty("spring.kafka.consumer.group-id")),
                JsonDeserializer.TRUSTED_PACKAGES, Objects.requireNonNull(env.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages")),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, Objects.requireNonNull(env.getProperty("spring.kafka.consumer.auto-offset-reset"))
        );
    }

}
