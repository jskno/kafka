package com.jskno.utils;

import com.jskno.avro.generated.Address;
import com.jskno.avro.generated.CoffeeOrderComplexIdV3;
import com.jskno.avro.generated.CoffeeOrderCreate;
import com.jskno.avro.generated.CoffeeOrderStatus;
import com.jskno.avro.generated.CoffeeOrderUpdate;
import com.jskno.avro.generated.OrderId;
import com.jskno.avro.generated.OrderLineItem;
import com.jskno.avro.generated.PickUp;
import com.jskno.avro.generated.Size;
import com.jskno.avro.generated.Store;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CoffeeOrderMultyplesSchemasUtil {

    public static CoffeeOrderCreate buildNewCoffeeOrder() {

        var orderId=   OrderId.newBuilder()
                .setId(randomId())
                .build();

        return CoffeeOrderCreate.newBuilder()
            .setId(orderId)
            .setUUID(UUID.randomUUID())
            .setName("Dilip Sundarraj")
            .setNickname("Primary Nickname")
            .setAdditionalNickname("Second Nickname")
            .setStore(generateStore())
            .setOrderLineItems(generateOrderLineItems())
            .setOrderTime(Instant.now())
            .setOrderDate(LocalDate.now())
            .setPickUp(PickUp.IN_STORE)
//            .setStatus(CoffeeOrderStatus.PROCESSING)
            .build();

    }

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

    public static CoffeeOrderUpdate buildCoffeeOrderUpdate(OrderId orderId) {
        return CoffeeOrderUpdate.newBuilder()
            .setId(orderId)
            .setStatus(CoffeeOrderStatus.PROCESSING)
            .build();
    }
}
