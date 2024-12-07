package com.jskno.email.notification.consumer;

import com.jskno.email.notification.entity.ProcessEventEntity;
import com.jskno.email.notification.repository.ProcessEventRepository;
import com.jskno.kafka.event.driven.ProductCreatedEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@EmbeddedKafka
@SpringBootTest(properties = {
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
class ProductCreatedEventConsumerV7TransactionalIntegrationTest {

    @MockitoBean
    private ProcessEventRepository processEventRepository;

    @MockitoBean
    private RestTemplate restTemplate;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockitoSpyBean
    ProductCreatedEventConsumerV7Transactional consumerV7Transactional;

    @Test
    void testProductCreatedEventHandler_onProductCreated() throws ExecutionException, InterruptedException {

        // Given
        ProductCreatedEvent event = ProductCreatedEvent.builder()
                .id(UUID.randomUUID().toString())
                .title("Test product")
                .price(new BigDecimal("10"))
                .quantity(1)
                .build();

        String messageId = UUID.randomUUID().toString();
        String messageKey = event.getId();

        ProducerRecord<String, Object> record = new ProducerRecord<>(
                "product-created-events-topic",
                messageKey,
                event);

        record.headers().add("messageId", messageId.getBytes(StandardCharsets.UTF_8));
        record.headers().add(KafkaHeaders.RECEIVED_KEY, messageKey.getBytes(StandardCharsets.UTF_8));

        Optional<ProcessEventEntity> eventEntity = Optional.of(new ProcessEventEntity());
        Mockito.when(processEventRepository.findByMessageId(Mockito.anyString())).thenReturn(eventEntity);
        Mockito.when(processEventRepository.save(Mockito.any())).thenReturn(eventEntity);

        String responseBody = "{\"key\":\"value\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);

        Mockito.when(restTemplate.exchange(
                        Mockito.anyString(),
                        Mockito.any(HttpMethod.class),
                        Mockito.isNull(),
                        Mockito.eq(String.class)))
                .thenReturn(responseEntity);

        // When
        kafkaTemplate.send(record).get();

        // Then
        ArgumentCaptor<String> messageIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> originCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ProductCreatedEvent> eventCaptor = ArgumentCaptor.forClass(ProductCreatedEvent.class);

        verify(consumerV7Transactional, timeout(5000).times(1)).handle(eventCaptor.capture(),
                messageIdCaptor.capture(),
                messageKeyCaptor.capture(),
                originCaptor.capture());

        assertEquals(messageId, messageIdCaptor.getValue());
        assertEquals(messageKey, messageKeyCaptor.getValue());
        Assertions.assertNull(originCaptor.getValue());
        assertEquals(event.getId(), eventCaptor.getValue().getId());
    }

}
