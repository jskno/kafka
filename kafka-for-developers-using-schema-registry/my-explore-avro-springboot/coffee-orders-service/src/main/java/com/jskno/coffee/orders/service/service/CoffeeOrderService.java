package com.jskno.coffee.orders.service.service;

import com.jskno.avro.generated.Address;
import com.jskno.avro.generated.CoffeeOrderCreate;
import com.jskno.avro.generated.CoffeeOrderStatus;
import com.jskno.avro.generated.CoffeeOrderUpdate;
import com.jskno.avro.generated.OrderId;
import com.jskno.avro.generated.OrderLineItem;
import com.jskno.avro.generated.PickUp;
import com.jskno.avro.generated.Size;
import com.jskno.avro.generated.Store;
import com.jskno.coffee.orders.service.dto.AddressDTO;
import com.jskno.coffee.orders.service.dto.CoffeeOrderCreateDTO;
import com.jskno.coffee.orders.service.dto.CoffeeOrderUpdateDTO;
import com.jskno.coffee.orders.service.dto.OrderLineItemDTO;
import com.jskno.coffee.orders.service.dto.StoreDTO;
import com.jskno.coffee.orders.service.producer.CofferOrderCreateProducer;
import com.jskno.coffee.orders.service.producer.CofferOrderUpdateProducer;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CoffeeOrderService {

    private final Random random;
    private final CofferOrderCreateProducer cofferOrderCreateProducer;
    private final CofferOrderUpdateProducer cofferOrderUpdateProducer;


    public CoffeeOrderService(CofferOrderCreateProducer cofferOrderCreateProducer,
        CofferOrderUpdateProducer cofferOrderUpdateProducer) {
        this.random =  new Random();
        this.cofferOrderCreateProducer = cofferOrderCreateProducer;
        this.cofferOrderUpdateProducer = cofferOrderUpdateProducer;
    }

    public CoffeeOrderCreateDTO createCoffeeOrder(CoffeeOrderCreateDTO coffeeOrderCreateDTO) {
        CoffeeOrderCreate coffeeOrderCreate = mapToCoffeeOrder(coffeeOrderCreateDTO);
        cofferOrderCreateProducer.sendMessage(coffeeOrderCreate);
        updateCoffeeOrderCreateDTO(coffeeOrderCreateDTO, coffeeOrderCreate);
        return coffeeOrderCreateDTO;
    }

    private CoffeeOrderCreate mapToCoffeeOrder(CoffeeOrderCreateDTO coffeeOrderCreateDTO) {

        return CoffeeOrderCreate.newBuilder()
            .setId(OrderId.newBuilder()
                .setBusinessId(random.nextLong())
                .setUUID(UUID.randomUUID())
                .build())
            .setName(coffeeOrderCreateDTO.getName())
            .setNickname(coffeeOrderCreateDTO.getNickName())
            .setStore(buildStore(coffeeOrderCreateDTO.getStore()))
            .setOrderLineItems(buildOrderLineItems(coffeeOrderCreateDTO.getOrderLineItems()))
            .setPickUp(PickUp.valueOf(coffeeOrderCreateDTO.getPickUp().name()))
            .setOrderTime(coffeeOrderCreateDTO.getOffsetDateTime().toInstant())
            .setOrderTime2(coffeeOrderCreateDTO.getLocalDateTime().toInstant(ZoneOffset.UTC))
            .setOrderDate(coffeeOrderCreateDTO.getLocalDate())
            .setStatus(CoffeeOrderStatus.valueOf(coffeeOrderCreateDTO.getStatus().name()))
            .build();
    }
    private Store buildStore(StoreDTO storeDTO) {
        return Store.newBuilder()
            .setId(storeDTO.getId())
            .setAddress(buildAddress(storeDTO.getAddress()))
            .build();
    }

    private Address buildAddress(AddressDTO addressDTO) {
        return Address.newBuilder()
            .setAddressLine1(addressDTO.getAddressLine1())
            .setCity(addressDTO.getCity())
            .setStateProvince(addressDTO.getState())
            .setCountry(addressDTO.getCountry())
            .setZip(addressDTO.getZip())
            .build();
    }


    private List<OrderLineItem> buildOrderLineItems(List<OrderLineItemDTO> orderLineItemsDTO) {
        return orderLineItemsDTO.stream()
            .map(this::buildOrderLineItem)
            .collect(Collectors.toList());
    }

    private OrderLineItem buildOrderLineItem(OrderLineItemDTO orderLineItemDTO) {
        return OrderLineItem.newBuilder()
            .setName(orderLineItemDTO.getName())
            .setSize(Size.valueOf(orderLineItemDTO.getSize().name()))
            .setQuantity(orderLineItemDTO.getQuantity())
            .setCost(orderLineItemDTO.getCost())
            .build();
    }

    private void updateCoffeeOrderCreateDTO(CoffeeOrderCreateDTO coffeeOrderCreateDTO, CoffeeOrderCreate coffeeOrderCreate) {
        coffeeOrderCreateDTO.getId().setBusinessId(coffeeOrderCreate.getId().getBusinessId());
        coffeeOrderCreateDTO.getId().setUUID(coffeeOrderCreate.getId().getUUID());
    }

    public CoffeeOrderUpdateDTO updateCoffeeOrder(CoffeeOrderUpdateDTO coffeeOrderUpdateDTO) {
        CoffeeOrderUpdate coffeeOrderUpdate = mapToCoffeeOrderUpdate(coffeeOrderUpdateDTO);
        cofferOrderUpdateProducer.sendMessage(coffeeOrderUpdate);
        return coffeeOrderUpdateDTO;
    }

    private CoffeeOrderUpdate mapToCoffeeOrderUpdate(CoffeeOrderUpdateDTO coffeeOrderUpdateDTO) {
        return CoffeeOrderUpdate.newBuilder()
            .setId(OrderId.newBuilder()
                .setUUID(coffeeOrderUpdateDTO.getId().getUUID())
                .setBusinessId(coffeeOrderUpdateDTO.getId().getBusinessId())
                .build())
            .setStatus(CoffeeOrderStatus.valueOf(coffeeOrderUpdateDTO.getStatus().name()))
            .build();
    }
}
