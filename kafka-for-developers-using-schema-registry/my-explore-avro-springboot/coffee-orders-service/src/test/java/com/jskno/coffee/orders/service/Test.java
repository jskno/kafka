package com.jskno.coffee.orders.service;

import java.time.LocalDate;

public class Test {

    @org.junit.jupiter.api.Test
    void test() {
        LocalDate yesterday = LocalDate.of(2024, 10, 17);
        System.out.println(yesterday.toEpochDay());

    }

}
