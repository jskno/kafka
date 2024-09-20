package com.jskno.coffee.orders.service.controller;

import com.jskno.coffee.orders.service.dto.CoffeeOrderCreateDTO;
import com.jskno.coffee.orders.service.dto.CoffeeOrderUpdateDTO;
import com.jskno.coffee.orders.service.service.CoffeeOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/coffee-orders")
@RequiredArgsConstructor
@Slf4j
public class CoffeeOrderController {

    private final CoffeeOrderService coffeeOrderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CoffeeOrderCreateDTO createOrder(@RequestBody CoffeeOrderCreateDTO coffeeOrderCreateDTO) {
        log.info("Received the request for an order: {}", coffeeOrderCreateDTO);
        return coffeeOrderService.createCoffeeOrder(coffeeOrderCreateDTO);
    }

    @PutMapping("/{businessId}")
    @ResponseStatus(HttpStatus.OK)
    public CoffeeOrderUpdateDTO updateOrder(
        @PathVariable Long businessId,
        @RequestBody CoffeeOrderUpdateDTO coffeeOrderUpdate) {
        log.info("Received the request to updated an order with id: {} and order: {}", businessId, coffeeOrderUpdate);
        assert businessId.equals(coffeeOrderUpdate.getId().getBusinessId());
        return coffeeOrderService.updateCoffeeOrder(coffeeOrderUpdate);

    }

}
