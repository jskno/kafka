package com.jskno.my.library.events.consumer.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jskno.my.library.events.consumer.consumer.LibraryEventsConsumer;
import com.jskno.my.library.events.consumer.entity.Book;
import com.jskno.my.library.events.consumer.entity.LibraryEvent;
import com.jskno.my.library.events.consumer.entity.LibraryEventType;
import com.jskno.my.library.events.consumer.jpa.FailureRecordRepository;
import com.jskno.my.library.events.consumer.jpa.LibraryEventsRepository;
import com.jskno.my.library.events.consumer.service.LibraryEventsService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@EmbeddedKafka(topics = {"library-events", "library-events.RETRY", "library-events.DLT"}, partitions = 3)
//@TestPropertySource(properties = {
//    "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
//    "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"
//})
@TestPropertySource(properties = {
    "topics.retry.startup=false"
})
@ActiveProfiles("test")
public class LibraryEventsConsumerIntegrationTest {

    @Value("${topics.retry}")
    private String retryTopic;

    @Value("${topics.dlt}")
    private String deadLetterTopic;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    KafkaListenerEndpointRegistry endpointRegistry;

    @SpyBean
    LibraryEventsConsumer libraryEventsConsumerSpy;

    @SpyBean
    LibraryEventsService libraryEventsServiceSpy;

    @Autowired
    LibraryEventsRepository libraryEventsRepository;

    @Autowired
    FailureRecordRepository failureRecordRepository;

    @Autowired
    ObjectMapper objectMapper;

    private Consumer<Integer, String> consumer;

    @BeforeEach
    void setup() {
        Mockito.reset(libraryEventsConsumerSpy);
        Mockito.reset(libraryEventsServiceSpy);

        MessageListenerContainer container = endpointRegistry.getListenerContainers()
            .stream().filter(messageListenerContainer ->
                Objects.equals(messageListenerContainer.getGroupId(), "library-events-listener-group"))
            .collect(Collectors.toList()).get(0);
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());

//        for (MessageListenerContainer messageListenerContainer : endpointRegistry.getListenerContainers()) {
//            System.out.println("Group Id : "+ messageListenerContainer.getGroupId());
//            if(Objects.equals(messageListenerContainer.getGroupId(), "library-events-listener-group")){
//                System.out.println("Waiting for assignment");
//                ContainerTestUtils.waitForAssignment(messageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());
//            }
//        }
    }

    @AfterEach
    void tearDown() {

        libraryEventsRepository.deleteAll();
//        failureRecordRepository.deleteAll();

    }

    @Test
    void publisNewLibraryEvent() throws ExecutionException, InterruptedException, JsonProcessingException {

        // Given
        String json = "{\"libraryEventId\":null,\"libraryEventType\":\"NEW\",\"book\":{\"bookId\":456,\"title\":\"Kafka Using Spring Boot\",\"author\":\"Dilip\"}}";

        // When
        kafkaTemplate.sendDefault(json).get();

        // Then
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);

        verify(libraryEventsConsumerSpy, times(1)).onMessage(isA(ConsumerRecord.class));
        verify(libraryEventsServiceSpy, times(1)).processLibraryEvent(isA(ConsumerRecord.class));

        List<LibraryEvent> libraryEventList = (List<LibraryEvent>) libraryEventsRepository.findAll();
        assert libraryEventList.size() == 1;
        libraryEventList.forEach(libraryEvent -> {
            assert libraryEvent.getLibraryEventId() != null;
            assertEquals(456, libraryEvent.getBook().getBookId());
        });

    }

    @Test
    void publishUpdateLibraryEvent() throws JsonProcessingException, ExecutionException, InterruptedException {

        // Given
        String json = "{\"libraryEventId\":null,\"libraryEventType\":\"NEW\",\"book\":{\"bookId\":456,\"title\":\"Kafka Using Spring Boot\",\"author\":\"Dilip\"}}";
        LibraryEvent libraryEvent = objectMapper.readValue(json, LibraryEvent.class);
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        libraryEventsRepository.save(libraryEvent);

        // Publish the update LibraryEvent
        Book updatedBook = Book.builder().
            bookId(456).title("Kafka Using Spring Boot 2.x").author("Dilip").build();
        libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
        libraryEvent.setBook(updatedBook);
        String updatedJson = objectMapper.writeValueAsString(libraryEvent);

        // When
        kafkaTemplate.sendDefault(libraryEvent.getLibraryEventId(), updatedJson).get();

        // Then
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);

        verify(libraryEventsConsumerSpy, times(1)).onMessage(isA(ConsumerRecord.class));
        verify(libraryEventsServiceSpy, times(1)).processLibraryEvent(isA(ConsumerRecord.class));
        LibraryEvent persistedLibraryEvent = libraryEventsRepository.findById(libraryEvent.getLibraryEventId()).get();
        assertEquals("Kafka Using Spring Boot 2.x", persistedLibraryEvent.getBook().getTitle());
    }

    @Test
    void publishModifyLibraryEvent_Not_A_Valid_LibraryEventId() throws JsonProcessingException, InterruptedException, ExecutionException {

        // Given
        Integer libraryEventId = 123;
        String json = "{\"libraryEventId\":" + libraryEventId + ",\"libraryEventType\":\"UPDATE\",\"book\":{\"bookId\":456,\"bookName\":\"Kafka Using Spring Boot\",\"bookAuthor\":\"Dilip\"}}";
        System.out.println(json);

        // When
        kafkaTemplate.sendDefault(libraryEventId, json).get();

        // Then
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(5, TimeUnit.SECONDS);


        verify(libraryEventsConsumerSpy, times(1)).onMessage(isA(ConsumerRecord.class));
        verify(libraryEventsServiceSpy, times(1)).processLibraryEvent(isA(ConsumerRecord.class));

        Optional<LibraryEvent> libraryEventOptional = libraryEventsRepository.findById(libraryEventId);
        assertFalse(libraryEventOptional.isPresent());

        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group2", "true", embeddedKafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, deadLetterTopic);

        ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, deadLetterTopic);

        System.out.println("consumer Record in deadletter topic : " + consumerRecord.value());

        assertEquals(json, consumerRecord.value());
        consumerRecord.headers()
            .forEach(header -> {
                System.out.println("Header Key : " + header.key() + ", Header Value : " + new String(header.value()));
            });
    }

    @Test
    void publishModifyLibraryEvent_Null_LibraryEventId() throws JsonProcessingException, InterruptedException, ExecutionException {

        // Given
        Integer libraryEventId = null;
        String json = "{\"libraryEventId\":" + libraryEventId + ",\"libraryEventType\":\"UPDATE\",\"book\":{\"bookId\":456,\"title\":\"Kafka Using Spring Boot\",\"author\":\"Dilip\"}}";

        // When
        kafkaTemplate.sendDefault(libraryEventId, json).get();

        // Then
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(5, TimeUnit.SECONDS);

        verify(libraryEventsConsumerSpy, times(1)).onMessage(isA(ConsumerRecord.class));
        verify(libraryEventsServiceSpy, times(1)).processLibraryEvent(isA(ConsumerRecord.class));

        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group3", "true", embeddedKafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, deadLetterTopic);

        ConsumerRecords<Integer, String> consumerRecords = KafkaTestUtils.getRecords(consumer);

        var deadletterList = new ArrayList<ConsumerRecord<Integer, String>>();
        consumerRecords.forEach((record) -> {
            if (record.topic().equals(deadLetterTopic)) {
                deadletterList.add(record);
            }
        });

        var finalList = deadletterList.stream()
            .filter(record -> record.value().equals(json))
            .collect(Collectors.toList());

        assert finalList.size() == 1;
    }

    @Test
    void publishModifyLibraryEvent_999_LibraryEventId() throws JsonProcessingException, InterruptedException, ExecutionException {

        // Given
        Integer libraryEventId = 999;
        String json = "{\"libraryEventId\":" + libraryEventId + ",\"libraryEventType\":\"UPDATE\",\"book\":{\"bookId\":456,\"title\":\"Kafka Using Spring Boot\",\"author\":\"Dilip\"}}";

        // When
        kafkaTemplate.sendDefault(libraryEventId, json).get();

        // Then
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(5, TimeUnit.SECONDS);

        verify(libraryEventsConsumerSpy, times(3)).onMessage(isA(ConsumerRecord.class));
        verify(libraryEventsServiceSpy, times(3)).processLibraryEvent(isA(ConsumerRecord.class));
    }

    @Test
    void publishModifyLibraryEvent_999_LibraryEventId_deadletterTopic()
        throws JsonProcessingException, InterruptedException, ExecutionException {

        // Given
        Integer libraryEventId = 999;
        String json = "{\"libraryEventId\":" + libraryEventId + ",\"libraryEventType\":\"UPDATE\",\"book\":{\"bookId\":456,\"bookName\":\"Kafka Using Spring Boot\",\"bookAuthor\":\"Dilip\"}}";

        // When
        kafkaTemplate.sendDefault(libraryEventId, json).get();

        // Then
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(5, TimeUnit.SECONDS);

        // Without Retry Listener
        verify(libraryEventsConsumerSpy, times(3)).onMessage(isA(ConsumerRecord.class));
        verify(libraryEventsServiceSpy, times(3)).processLibraryEvent(isA(ConsumerRecord.class));

        // With Retry listener
//        verify(libraryEventsConsumerSpy, atLeast(3)).onMessage(isA(ConsumerRecord.class));
//        verify(libraryEventsServiceSpy, atLeast(3)).processLibraryEvent(isA(ConsumerRecord.class));


        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, retryTopic);

        ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, retryTopic);

        System.out.println("consumer Record in deadletter topic : " + consumerRecord.value());

        assertEquals(json, consumerRecord.value());
        consumerRecord.headers()
            .forEach(header -> {
                System.out.println("Header Key : " + header.key() + ", Header Value : " + new String(header.value()));
            });
    }

    @Test
//    @Disabled
    void publishModifyLibraryEvent_Null_LibraryEventId_failureRecord_NotRecovery() throws JsonProcessingException, InterruptedException, ExecutionException {

        // Given
        String json = "{\"libraryEventId\":null,\"libraryEventType\":\"UPDATE\",\"book\":{\"bookId\":456,\"title\":\"Kafka Using Spring Boot\",\"author\":\"Dilip\"}}";

        // When
        kafkaTemplate.sendDefault(json).get();

        // Then
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(5, TimeUnit.SECONDS);


        verify(libraryEventsConsumerSpy, times(1)).onMessage(isA(ConsumerRecord.class));
        verify(libraryEventsServiceSpy, times(1)).processLibraryEvent(isA(ConsumerRecord.class));


        var failureCount = failureRecordRepository.count();
        assertEquals(1, failureCount);
        failureRecordRepository.findAll().forEach(failureRecord -> {
            System.out.println("failureRecord : " + failureRecord);
        });

    }

    @Test
//    @Disabled
    void publishModifyLibraryEvent_999_LibraryEventId_failureRecord_Recovery() throws JsonProcessingException, InterruptedException, ExecutionException {

        // Given
        Integer libraryEventId = 999;
        String json = "{\"libraryEventId\":" + libraryEventId + ",\"libraryEventType\":\"UPDATE\",\"book\":{\"bookId\":456,\"title\":\"Kafka Using Spring Boot\",\"author\":\"Dilip\"}}";

        // When
        kafkaTemplate.sendDefault(libraryEventId, json).get();

        // Then
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(5, TimeUnit.SECONDS);


        verify(libraryEventsConsumerSpy, times(3)).onMessage(isA(ConsumerRecord.class));
        verify(libraryEventsServiceSpy, times(3)).processLibraryEvent(isA(ConsumerRecord.class));


        var failureCount = failureRecordRepository.count();
        assertEquals(1, failureCount);
        failureRecordRepository.findAll().forEach(failureRecord -> {
            System.out.println("failureRecord : " + failureRecord);
        });

    }

}
