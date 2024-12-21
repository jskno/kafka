package com.jskno.payments.service.handler;

import com.jskno.core.dto.Payment;
import com.jskno.core.dto.commands.ProcessPaymentCommand;
import com.jskno.core.dto.events.PaymentFailedEvent;
import com.jskno.core.dto.events.PaymentProcessedEvent;
import com.jskno.core.exceptions.CreditCardProcessorUnavailableException;
import com.jskno.payments.service.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${payments.commands.topic.name}")
@Slf4j
public class PaymentCommadsHandler {

    private final PaymentService paymentService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String paymentsEventsTopic;

    public PaymentCommadsHandler(PaymentService paymentService,
                                 KafkaTemplate<String, Object> kafkaTemplate,
                                 @Value("${payments.events.topic.name}") String paymentsEventsTopic) {
        this.paymentService = paymentService;
        this.kafkaTemplate = kafkaTemplate;
        this.paymentsEventsTopic = paymentsEventsTopic;
    }

    @KafkaHandler
    public void handleCommand(@Payload ProcessPaymentCommand command) {

        try {
            Payment payment = Payment.builder()
                    .orderId(command.getOrderId())
                    .productId(command.getProductId())
                    .productPrice(command.getProductPrice())
                    .productQuantity(command.getProductQuantity())
                    .build();
            Payment processedPayment = paymentService.process(payment);

            PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                    .orderId(processedPayment.getOrderId())
                    .paymentId(processedPayment.getId())
                    .build();
            kafkaTemplate.send(paymentsEventsTopic, event);

        } catch (CreditCardProcessorUnavailableException ex) {
            log.error(ex.getLocalizedMessage(), ex);
            PaymentFailedEvent paymentFailedEvent = PaymentFailedEvent.builder()
                    .orderId(command.getOrderId())
                    .productId(command.getProductId())
                    .productQuantity(command.getProductQuantity())
                    .build();
            kafkaTemplate.send(paymentsEventsTopic, paymentFailedEvent);
        }
    }
}