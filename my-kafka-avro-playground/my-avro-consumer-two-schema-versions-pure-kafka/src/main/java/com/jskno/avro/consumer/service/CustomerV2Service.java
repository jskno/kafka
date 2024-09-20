package com.jskno.avro.consumer.service;

import com.jskno.confluent.cloud.model.customer.Customer;
import com.jskno.confluent.cloud.model.customer.CustomerV2;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;

@Slf4j
public class CustomerV2Service {

    public void processCustomer(GenericRecord genericRecord) {
        CustomerV2 customer = (CustomerV2) genericRecord;
        log.info("Id: {}, Name: {}, FaxNumber: {}", customer.getId(), customer.getName(), customer.getEmail());
    }

}
