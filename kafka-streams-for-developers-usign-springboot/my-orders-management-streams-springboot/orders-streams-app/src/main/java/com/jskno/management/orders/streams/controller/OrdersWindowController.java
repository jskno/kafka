package com.jskno.management.orders.streams.controller;

import com.jskno.management.orders.domain.dto.OrderCountPerStoreByWindowDTO;
import com.jskno.management.orders.domain.dto.OrdersRevenuePerStoreByWindowsDTO;
import com.jskno.management.orders.streams.service.OrderWindowService;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrdersWindowController {

    private final OrderWindowService orderWindowService;

    @GetMapping("/windows/count/{orderType}")
    public List<OrderCountPerStoreByWindowDTO> getOrdersCountWindowByType(@PathVariable String orderType) {
        return orderWindowService.getOrdersCountWindowByType(orderType);
    }

    @GetMapping("/windows/count")
    public List<OrderCountPerStoreByWindowDTO> getAllOrdersCountByWindow() {
        return orderWindowService.getAllOrdersCountByWindow();
    }

    @GetMapping("/windows/count/with-times")
    public List<OrderCountPerStoreByWindowDTO> getAllOrdersCountByWindowAndTimes(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fromTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime toTime) {
        return orderWindowService.getAllOrdersCountByWindowAndTimes(fromTime, toTime);
    }

    @GetMapping("/windows/revenue/{orderType}")
    public List<OrdersRevenuePerStoreByWindowsDTO> getOrdersRevenue(@PathVariable String orderType) {
        return orderWindowService.getOrdersRevenueWindowByType(orderType);
    }



}
