package com.jskno.payments.service;

import org.springframework.boot.SpringApplication;

public class TestPaymentsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(PaymentsServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
