package com.jskno.avro.consumer.service;

import com.jskno.confluent.cloud.model.customer.Customer;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;

@Slf4j
public class CustomerV1Service {

    public void processCustomer(GenericRecord genericRecord) {
        Customer customer = (Customer) genericRecord;
        log.info("Id: {}, Name: {}, FaxNumber: {}", customer.getId(), customer.getName(), customer.getFaxNumber());
    }

}
