package com.jskno.orders.service.saga;

import com.jskno.core.dto.commands.*;
import com.jskno.core.dto.events.*;
import com.jskno.core.types.OrderStatus;
import com.jskno.orders.service.service.OrderHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {
        "${orders.events.topic.name}",
        "${products.events.topic.name}",
        "${payments.events.topic.name}"
})
@Slf4j
public class OrderSaga {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderHistoryService orderHistoryService;
    private final String productsCommandTopic;
    private final String processPaymentCommandTopic;
    private final String ordersCommandTopic;

    public OrderSaga(KafkaTemplate<String, Object> kafkaTemplate, OrderHistoryService orderHistoryService,
                     @Value("${products.commands.topic.name}") String productsCommandTopic,
                     @Value("${payments.commands.topic.name}") String processPaymentCommandTopic,
                     @Value("${orders.commands.topic.name}") String ordersCommandTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderHistoryService = orderHistoryService;
        this.productsCommandTopic = productsCommandTopic;
        this.processPaymentCommandTopic = processPaymentCommandTopic;
        this.ordersCommandTopic = ordersCommandTopic;
    }

    @KafkaHandler
    public void handleEvent(@Payload OrderCreatedEvent orderCreatedEvent) {

        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .orderId(orderCreatedEvent.getOrderId())
                .productId(orderCreatedEvent.getProductId())
                .productQuantity(orderCreatedEvent.getProductQuantity())
                .build();

        kafkaTemplate.send(productsCommandTopic, reserveProductCommand);
        orderHistoryService.add(orderCreatedEvent.getOrderId(), OrderStatus.CREATED);
    }

    @KafkaHandler
    public void handleEvent(@Payload ProductReservedEvent productReservedEvent) {

        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .productId(productReservedEvent.getProductId())
                .productQuantity(productReservedEvent.getProductQuantity())
                .productPrice(productReservedEvent.getProductPrice())
                .build();

        kafkaTemplate.send(processPaymentCommandTopic, processPaymentCommand);
    }

    @KafkaHandler
    public void handleEvent(@Payload PaymentProcessedEvent event) {
        ApproveOrderCommand approveOrderCommand = ApproveOrderCommand.builder()
                .orderId(event.getOrderId())
                .build();
        kafkaTemplate.send(ordersCommandTopic, approveOrderCommand);
    }

    @KafkaHandler
    public void handleEvent(@Payload OrderApprovedEvent event) {
        orderHistoryService.add(event.getOrderId(), OrderStatus.APPROVED);
    }

    @KafkaHandler
    public void handleEvent(@Payload PaymentFailedEvent event) {
        CancelProductReservationCommand command = CancelProductReservationCommand.builder()
                .orderId(event.getOrderId())
                .productId(event.getProductId())
                .productQuantity(event.getProductQuantity())
                .build();

        kafkaTemplate.send(productsCommandTopic, command);
    }

    @KafkaHandler
    public void handleEvent(@Payload ProductReservationCancelledEvent event) {
        RejectOrderCommand command = RejectOrderCommand.builder().orderId(event.getOrderId()).build();
        kafkaTemplate.send(ordersCommandTopic, command);

        orderHistoryService.add(event.getOrderId(), OrderStatus.REJECTED);
    }
}
