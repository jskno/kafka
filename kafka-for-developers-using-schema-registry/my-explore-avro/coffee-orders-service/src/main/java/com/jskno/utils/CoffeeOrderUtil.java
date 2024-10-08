package com.jskno.utils;

import com.jskno.avro.generated.Address;
import com.jskno.avro.generated.CoffeeOrder;
import com.jskno.avro.generated.OrderLineItem;
import com.jskno.avro.generated.Size;
import com.jskno.avro.generated.Store;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CoffeeOrderUtil {

    public static CoffeeOrder buildNewCoffeeOrder() {

        return CoffeeOrder.newBuilder()
            .setId(randomId())
            .setUUID(UUID.randomUUID())
            .setName("Dilip Sundarraj")
            //.setNickName("DS")
            //.setFullName("Dilip Sundarraj")
            .setStore(generateStore())
            .setOrderLineItems(generateOrderLineItems())
            .setOrderTime(Instant.now())
            .setOrderDate(LocalDate.now())
//            .setPickUp(PickUp.IN_STORE)
            // .setPickUpType(PickUp.IN_STORE)
            .setStatus("NEW")
            .build();

    }

//    public static CoffeeOrderOld buildNewCoffeeOrderV2(){
//
//        return CoffeeOrderOld.newBuilder()
//                .setId(randomId())
//                .setName("Chicago 1234")
//                .setStore(generateStore())
//                .setOrderLineItems(generateOrderLineItems())
//                .build();
//
//
//    }

    private static List<OrderLineItem> generateOrderLineItems() {

        var orderLineItem = OrderLineItem.newBuilder()
            .setName("Caffe Latte")
            .setQuantity(1)
            .setSize(Size.MEDIUM)
            .setCost(BigDecimal.valueOf(3.99))
            .build();

        return List.of(orderLineItem);

    }

    private static Store generateStore() {

        return Store.newBuilder()
            .setId(randomId())
            .setAddress(buildAddress())
            .build();
    }

    private static Address buildAddress() {

        return Address.newBuilder()
            .setAddressLine1("1234 Address Line 1")
            .setCity("Chicago")
            .setStateProvince("IL")
            .setZip("12345")
            .setCountry("ESP")
            .build();

    }

    public static int randomId() {
        Random random = new Random();
        return random.nextInt(1000);
    }
}
