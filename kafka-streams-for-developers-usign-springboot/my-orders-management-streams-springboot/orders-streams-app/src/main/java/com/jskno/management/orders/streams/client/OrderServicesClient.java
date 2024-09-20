package com.jskno.management.orders.streams.client;

import com.jskno.management.orders.domain.dto.HostInfoDTO;
import com.jskno.management.orders.domain.dto.HostInfoWithKeyDTO;
import com.jskno.management.orders.domain.dto.OrderCountPerStoreDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class OrderServicesClient {

    private final WebClient webClient;

    public List<OrderCountPerStoreDTO> retrieveOrdersCountByOrderType(HostInfoDTO hostInfoDTO, String orderType) {
        String basePath = "http://" + hostInfoDTO.hostName() + ":" + hostInfoDTO.port();
        String url = UriComponentsBuilder
            .fromHttpUrl(basePath)
            .path("/v1/orders/count/{orderType}")
            .buildAndExpand(orderType)
            .toUriString();
        return webClient.get().uri(url).retrieve().bodyToFlux(OrderCountPerStoreDTO.class).collectList().block();
    }

    public OrderCountPerStoreDTO retrieveOrdersCountByOrderTypeAndLocationId(
        HostInfoWithKeyDTO hostInfoDTO,
        String orderType,
        String locationId) {

        String basePath = "http://" + hostInfoDTO.host() + ":" + hostInfoDTO.port();
        String url = UriComponentsBuilder
            .fromHttpUrl(basePath)
            .path("/v1/orders/count/{orderType}/location-id")
            .queryParam("locationId", locationId)
            .buildAndExpand(orderType)
            .toUriString();
        return webClient.get().uri(url).retrieve().bodyToMono(OrderCountPerStoreDTO.class).block();
    }
}
