package com.jskno.management.orders.streams.controller;

import com.jskno.management.orders.domain.dto.AllOrderCountPerStoreDTO;
import com.jskno.management.orders.domain.dto.OrderCountPerStoreDTO;
import com.jskno.management.orders.domain.dto.OrderRevenueDTO;
import com.jskno.management.orders.streams.service.OrderMultipleInstancesService;
import com.jskno.management.orders.streams.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrdersController {

    private final OrderService orderService;
    private final OrderMultipleInstancesService orderMultipleInstancesService;

    @GetMapping("count/{orderType}")
    public List<OrderCountPerStoreDTO> getInternalOrdersCountByType(@PathVariable String orderType) {
        return orderService.getOrdersCountByType(orderType);
    }

    @GetMapping("count/external/{orderType}")
    public List<OrderCountPerStoreDTO> getOrdersCountByType(@PathVariable String orderType) {
        return orderMultipleInstancesService.getOrdersCountByType(orderType);
    }


    @GetMapping("count/{orderType}/location-id")
    public OrderCountPerStoreDTO getInternalOrdersCountByLocationId(
        @PathVariable String orderType,
        @RequestParam String locationId) {
        return orderService.getOrdersCountByLocationId(orderType, locationId);
    }

    @GetMapping("count/external/{orderType}/location-id")
    public OrderCountPerStoreDTO getOrdersCountByLocationId(
        @PathVariable String orderType,
        @RequestParam String locationId) {
        return orderMultipleInstancesService.getOrdersCountByLocationId(orderType, locationId);
    }

    @GetMapping("count")
    public List<AllOrderCountPerStoreDTO> getAllOrdersCount() {
        return orderService.getAllOrdersCount();
    }

    @GetMapping("count/external")
    public List<AllOrderCountPerStoreDTO> getAllExternalOrdersCount() {
        return orderMultipleInstancesService.getAllOrdersCount();
    }

    @GetMapping("revenue/{orderType}")
    public List<OrderRevenueDTO> getOrdersRevenueByType(@PathVariable String orderType) {
        return orderService.getOrdersRevenueByType(orderType);
    }

    @GetMapping("revenue/{orderType}/location-id")
    public OrderRevenueDTO getOrdersRevenueByType(@PathVariable String orderType, @RequestParam String locationId) {
        return orderService.getOrdersRevenueByTypeAndLocationId(orderType, locationId);
    }
}
