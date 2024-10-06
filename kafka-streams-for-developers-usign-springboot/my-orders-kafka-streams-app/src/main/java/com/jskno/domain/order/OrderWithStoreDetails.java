package com.jskno.domain.order;

import com.jskno.domain.store.Store;
import lombok.Builder;

@Builder
public record OrderWithStoreDetails(
    Order order,
    Store store
){}
