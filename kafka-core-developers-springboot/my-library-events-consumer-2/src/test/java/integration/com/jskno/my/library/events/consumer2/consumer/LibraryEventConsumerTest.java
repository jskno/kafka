package com.jskno.my.library.events.consumer2.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jskno.my.library.events.consumer2.domain.BookDTO;
import com.jskno.my.library.events.consumer2.domain.LibraryEventDTO;
import com.jskno.my.library.events.consumer2.entity.Book;
import com.jskno.my.library.events.consumer2.entity.LibraryEvent;
import com.jskno.my.library.events.consumer2.entity.LibraryEventType;
import com.jskno.my.library.events.consumer2.repository.BookRepository;
import com.jskno.my.library.events.consumer2.repository.LibraryEventRepository;
import com.jskno.my.library.events.consumer2.service.LibraryEventService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@EmbeddedKafka(topics = {"library-events", "library-events-retry", "library-events-dlt"}, partitions = 3)
@TestPropertySource(properties = {
    "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.LongSerializer",
    "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
    "spring.kafka.template.default-topic=library-events",
    "library.events.kafka.startup.retry.topic=false"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LibraryEventConsumerTest {

    @Value("${library.events.kafka.dlt.topic}")
    private String deadLetterTopic;

    @Value("${library.events.kafka.retry.topic}")
    private String retryTopic;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    KafkaTemplate<Long, LibraryEventDTO> kafkaTemplate;

    @Autowired
    KafkaListenerEndpointRegistry endpointRegistry;

    @SpyBean
    A_LibraryEventsConsumer libraryEventsConsumer;

    @SpyBean
    LibraryEventService libraryEventService;

    @Autowired
    LibraryEventRepository libraryEventRepository;

    @Autowired
    BookRepository bookRepository;

    @BeforeEach
    void setup() {
        // We make sure here that the test waits until all the partitions are assigned to the consumer
        // So that the container does not have any issue reading the events
        endpointRegistry.getListenerContainers().stream()
            .filter(listenerContainer -> listenerContainer.getListenerId().equals("libraryEventListener"))
            .forEach(listenerContainer -> ContainerTestUtils.waitForAssignment(listenerContainer, embeddedKafkaBroker.getPartitionsPerTopic()));
    }

    @AfterEach
    void tearDown() {
        libraryEventRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    void testOnMessage_WhenNewLibraryEvent() throws ExecutionException, InterruptedException {
        // Given
        LibraryEventDTO libraryEventDTO = LibraryEventDTO.builder()
            .id(null)
            .type(LibraryEventType.NEW)
            .book(BookDTO.builder().id(456L).name("Confluent Certification").author("Jose Cano").build())
            .build();

        // When
        // The kafkaTemplate.send is an async call but adding the get we blocked the main thread and
        // wait until the new thread created by template send invocation finishes.
        // This is done because we can performe some further steps only after the event has been published successfully
        SendResult<Long, LibraryEventDTO> sendResult = kafkaTemplate.sendDefault(libraryEventDTO).get();

        // Then
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);

        // Then
        Mockito.verify(libraryEventsConsumer, Mockito.times(1))
            .onMessage(ArgumentMatchers.isA(ConsumerRecord.class));
        Mockito.verify(libraryEventService, Mockito.times(1))
            .processLibraryEvent(ArgumentMatchers.isA(LibraryEventDTO.class));

        List<LibraryEvent> all = libraryEventRepository.findAll();
        Assertions.assertEquals(1, all.size());
        all.forEach(libraryEvent -> {
            Assertions.assertNotNull(libraryEvent.getId());
            Assertions.assertEquals(libraryEventDTO.book().id(), libraryEvent.getBook().getId());
        });
    }

    @Test
    void testOnMessage_WhenUpdateLibraryEvent() throws ExecutionException, InterruptedException {
        LibraryEvent newLibraryEvent = LibraryEvent.builder()
            .id(null)
            .type(LibraryEventType.NEW)
            .book(Book.builder().id(456L).name("Confluent Certification").author("Jose Cano").build())
            .build();
        libraryEventRepository.save(newLibraryEvent);

        LibraryEventDTO libraryEventDTO = LibraryEventDTO.builder()
            .id(newLibraryEvent.getId())
            .type(LibraryEventType.UPDATE)
            .book(BookDTO.builder().id(456L)
                .name("Kafka, Spring for Kafka and Confluent Certification")
                .author("Jose Cano").build())
            .build();

        SendResult<Long, LibraryEventDTO> sendResult = kafkaTemplate.sendDefault(
            libraryEventDTO.id(), libraryEventDTO).get();

        // When
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);

        // Then
        // THIS IS NOT NEEDED WHEN WE HAVE A FINAL ASSERT BASED ON DDBB THAT IS THE ULTIMATELLY OUTPUT OF THE APP.
        Mockito.verify(libraryEventsConsumer, Mockito.times(1))
            .onMessage(ArgumentMatchers.isA(ConsumerRecord.class));
        Mockito.verify(libraryEventService, Mockito.times(1))
            .processLibraryEvent(ArgumentMatchers.isA(LibraryEventDTO.class));

        Optional<LibraryEvent> updatedLibraryEvent = libraryEventRepository.findById(libraryEventDTO.id());
        Assertions.assertTrue(updatedLibraryEvent.isPresent());
        Assertions.assertNotNull(updatedLibraryEvent.get().getId());
        Assertions.assertEquals(libraryEventDTO.book().id(), updatedLibraryEvent.get().getBook().getId());
        Assertions.assertEquals(libraryEventDTO.book().name(), updatedLibraryEvent.get().getBook().getName());
    }

    // IN THIS TEST WE SIMULATE WHAT SHOULD BE A INTERNAL APPLICATION PROBLEM SUCH AS THE VALIDATION HAS NOT PASSED
    // BECAUSE THE REQUEST IS MISSING SOMETHING, THE EXTERNAL API RETURN A CLIENT ERROR SO WE BUILD OUR REQUEST WRONGLY
    // IN THESE OPTIONS WE SHOULD RETURN A SPECIFIC EXCEPTION THAT MARKED THESE SCENARIOS AS NOT RECOVERABLE
    @Test
    void testOnMessage_WhenUpdateLibraryEvent_WithNullId() throws ExecutionException, InterruptedException {
        LibraryEventDTO libraryEventDTO = LibraryEventDTO.builder()
            .id(null)
            .type(LibraryEventType.UPDATE)
            .book(BookDTO.builder().id(456L)
                .name("Kafka, Spring for Kafka and Confluent Certification")
                .author("Jose Cano").build())
            .build();

        SendResult<Long, LibraryEventDTO> sendResult = kafkaTemplate.sendDefault(
            libraryEventDTO.id(), libraryEventDTO).get();

        // When
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(10, TimeUnit.SECONDS);

        // Then
        Mockito.verify(libraryEventsConsumer, Mockito.times(1))
            .onMessage(ArgumentMatchers.isA(ConsumerRecord.class));
        Mockito.verify(libraryEventService, Mockito.times(1))
            .processLibraryEvent(ArgumentMatchers.isA(LibraryEventDTO.class));

        try(Consumer<Long, LibraryEventDTO> recoverTopicsConsumer = createRecoverConsumer("group1")) {
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(recoverTopicsConsumer, deadLetterTopic);
        ConsumerRecord<Long, LibraryEventDTO> consumerRecord = KafkaTestUtils.getSingleRecord(recoverTopicsConsumer, deadLetterTopic);
        Assertions.assertEquals(libraryEventDTO, consumerRecord.value());

//        ConsumerRecords<Long, LibraryEventDTO> consumerRecords = KafkaTestUtils.getRecords(recoverTopicsConsumer);

//        List<ConsumerRecord<Long, LibraryEventDTO>> records = new ArrayList<>();
//        consumerRecords.forEach(records::add);

//        Assertions.assertTrue(records.stream().map(ConsumerRecord::value).anyMatch(event -> event.equals(libraryEventDTO)));

//        records.forEach(record -> record.headers()
//            .forEach(header -> {
//                System.out.println("Header Key : " + header.key() + ", Header Value : " + new String(header.value()));
//            }));
        }
    }

    // IN THIS TEST WE SIMULATE WHAT SHOULD BE A INTERNAL APPLICATION PROBLEM SUCH AS THE VALIDATION HAS NOT PASSED
    // BECAUSE THE REQUEST IS MISSING SOMETHING, THE EXTERNAL API RETURN A CLIENT ERROR SO WE BUILD OUR REQUEST WRONGLY
    // IN THESE OPTIONS WE SHOULD RETURN A SPECIFIC EXCEPTION THAT MARKED THESE SCENARIOS AS NOT RECOVERABLE
    @Test
    void testOnMessage_WhenUpdateLibraryEvent_WithNotExistingId() throws ExecutionException, InterruptedException {
        LibraryEventDTO libraryEventDTO = LibraryEventDTO.builder()
            .id(999L)
            .type(LibraryEventType.UPDATE)
            .book(BookDTO.builder().id(456L)
                .name("Kafka, Spring for Kafka and Confluent Certification")
                .author("Jose Cano").build())
            .build();

        SendResult<Long, LibraryEventDTO> sendResult = kafkaTemplate.sendDefault(
            libraryEventDTO.id(), libraryEventDTO).get();

        // When
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(10, TimeUnit.SECONDS);

        // Then
        Mockito.verify(libraryEventsConsumer, Mockito.times(1))
            .onMessage(ArgumentMatchers.isA(ConsumerRecord.class));
        Mockito.verify(libraryEventService, Mockito.times(1))
            .processLibraryEvent(ArgumentMatchers.isA(LibraryEventDTO.class));

        try(Consumer<Long, LibraryEventDTO> recoverTopicsConsumer = createRecoverConsumer("group2")) {
            embeddedKafkaBroker.consumeFromAnEmbeddedTopic(recoverTopicsConsumer,deadLetterTopic);
            ConsumerRecord<Long, LibraryEventDTO> consumerRecord = KafkaTestUtils.getSingleRecord(recoverTopicsConsumer, deadLetterTopic);

            System.out.println("consumer Record in deadletter topic : " + consumerRecord.value());

            assertEquals(libraryEventDTO, consumerRecord.value());
            consumerRecord.headers()
                .forEach(header -> {
                    System.out.println("Header Key : " + header.key() + ", Header Value : " + new String(header.value()));
                });
        }
    }

    // IN THIS TEST WE SIMULATE WHAT SHOULD BE A EXTERNAL DEPENDENCY PROBLEM SUCH AS THE DDBB IS DOWN
    // OR THE EXTERNAL API RETURNS TIME OUT,...
    // IN THESE OPTIONS WE SHOULD RETURN A SPECIFIC EXCEPTION THAT MARKED THESE SCENARIOS AS RECOVERABLE
    @Test
    void testOnMessage_WhenUpdateLibraryEvent_WithExternalDependencyDown() throws ExecutionException, InterruptedException {
        LibraryEventDTO libraryEventDTO = LibraryEventDTO.builder()
            .id(666L)
            .type(LibraryEventType.UPDATE)
            .book(BookDTO.builder().id(456L)
                .name("Kafka, Spring for Kafka and Confluent Certification")
                .author("Jose Cano").build())
            .build();

        SendResult<Long, LibraryEventDTO> sendResult = kafkaTemplate.sendDefault(
            libraryEventDTO.id(), libraryEventDTO).get();

        // When
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(10, TimeUnit.SECONDS);

        // Then
        Mockito.verify(libraryEventsConsumer, Mockito.times(3))
            .onMessage(ArgumentMatchers.isA(ConsumerRecord.class));
        Mockito.verify(libraryEventService, Mockito.times(3))
            .processLibraryEvent(ArgumentMatchers.isA(LibraryEventDTO.class));

        try(Consumer<Long, LibraryEventDTO> recoverTopicsConsumer = createRecoverConsumer("group3")) {

            embeddedKafkaBroker.consumeFromAnEmbeddedTopic(recoverTopicsConsumer,retryTopic);
            ConsumerRecord<Long, LibraryEventDTO> consumerRecord = KafkaTestUtils.getSingleRecord(recoverTopicsConsumer, retryTopic);

            System.out.println("consumer Record in retry topic : " + consumerRecord.value());

            assertEquals(libraryEventDTO, consumerRecord.value());
            consumerRecord.headers()
                .forEach(header -> {
                    System.out.println("Header Key : " + header.key() + ", Header Value : " + new String(header.value()));
                });
        }
    }

    private Consumer<Long, LibraryEventDTO> createRecoverConsumer(String groupId) {
        var jsonDes = new JsonDeserializer<>(LibraryEventDTO.class,false);
        Map<String, Object> props = KafkaTestUtils
            .consumerProps(groupId, "true", embeddedKafkaBroker);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
//        props.put("spring.kafka.consumer.properties.spring.json.value.default.type", "com.jskno.mylibraryeventsproducer2.domain.LibraryEventDTO");
//        props.put("spring.kafka.consumer.properties.spring.json.add.type.headers", false);
        props.put("spring.kafka.consumer.properties.spring.json.trusted.packages", "*");
        return new DefaultKafkaConsumerFactory<>(props, new LongDeserializer(), jsonDes).createConsumer();
    }

}
