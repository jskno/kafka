package com.jskno.greetings.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Pair<L,R> {

    private L l;
    private R r;
}
