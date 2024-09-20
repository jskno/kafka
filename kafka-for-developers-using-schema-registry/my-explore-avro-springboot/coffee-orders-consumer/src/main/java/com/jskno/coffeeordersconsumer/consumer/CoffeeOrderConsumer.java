package com.jskno.coffeeordersconsumer.consumer;

import com.jskno.avro.generated.CoffeeOrderCreate;
import com.jskno.avro.generated.CoffeeOrderUpdate;
import com.jskno.avro.generated.OrderId;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CoffeeOrderConsumer {

    @KafkaListener(
        id = "coffee-order-consumer",
        topics = "${kafka.coffee.orders.topic.name}",
        autoStartup = "${kafka.coffee.orders.listener.enabled}",
        groupId = "${kafka.coffee.orders.topic.group.id}"
    )
    public void onMessage(ConsumerRecord<OrderId, GenericRecord> consumerRecord) {
        GenericRecord genericRecord = consumerRecord.value();
        log.info("Event received with key: {}, and value: {}", consumerRecord.key(), genericRecord);

        if (genericRecord instanceof CoffeeOrderCreate cofferOrderCreate) {
            log.info(cofferOrderCreate.getName());
        } else if (genericRecord instanceof CoffeeOrderUpdate coffeeOrderUpdate) {
            log.info(coffeeOrderUpdate.getStatus().name());
        }

    }

}
