package com.jskno.mylibraryeventsproducer2.controller;

import com.jskno.mylibraryeventsproducer2.domain.LibraryEventDTO;
import com.jskno.mylibraryeventsproducer2.util.TestUtil;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = "library-events")
@TestPropertySource(properties = {
    "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.admin.properties.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.template.default-topic=library-events",
})
//@ActiveProfiles("local")
class LibraryEventControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<Long, LibraryEventDTO> consumer;

    @BeforeEach
    void setUp() {
        var jsonDes = new JsonDeserializer<>(LibraryEventDTO.class,false);
        Map<String, Object> props = KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put("spring.kafka.consumer.properties.spring.json.value.default.type", "com.jskno.mylibraryeventsproducer2.domain.LibraryEventDTO");
        props.put("spring.kafka.consumer.properties.spring.json.add.type.headers", false);
        props.put("spring.kafka.consumer.properties.spring.json.trusted.packages", "*");
        consumer = new DefaultKafkaConsumerFactory<>(props, new LongDeserializer(), jsonDes).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    void createLibraryEvent() {
        // Given
        LibraryEventDTO libraryEvent = TestUtil.buildLibraryEvent();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<LibraryEventDTO> httpEntity = new HttpEntity<>(libraryEvent, httpHeaders);

        // When
        ResponseEntity<LibraryEventDTO> responseEntity = restTemplate.exchange(
            "/v1/library-events", HttpMethod.POST, httpEntity, LibraryEventDTO.class);

        // Then
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        ConsumerRecords<Long, LibraryEventDTO> records = KafkaTestUtils.getRecords(consumer);
        Assertions.assertEquals(1, records.count());
        records.forEach(record -> {
            Assertions.assertEquals(libraryEvent, record.value());
        });

    }

    @Test
    void createSyncLibraryEvent() {
        // Given
        LibraryEventDTO libraryEvent = TestUtil.buildLibraryEvent();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<LibraryEventDTO> httpEntity = new HttpEntity<>(libraryEvent, httpHeaders);

        // When
        ResponseEntity<LibraryEventDTO> responseEntity = restTemplate.exchange(
            "/v1/library-events/sync", HttpMethod.POST, httpEntity, LibraryEventDTO.class);

        // Then
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        ConsumerRecords<Long, LibraryEventDTO> records = KafkaTestUtils.getRecords(consumer);
        Assertions.assertEquals(2, records.count());
        records.forEach(record -> {
            Assertions.assertEquals(record.value(), libraryEvent);
        });

    }

    @Test
    void createLibraryEventWithRecord() {
    }

    @Test
    void createLibraryEventWithHeaders() {
    }
}