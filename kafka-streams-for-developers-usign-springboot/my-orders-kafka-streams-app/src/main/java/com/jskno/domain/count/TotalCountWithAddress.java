package com.jskno.domain.count;

import com.jskno.domain.store.Store;

public record TotalCountWithAddress(
    Long count,
    Store store
) {

}
