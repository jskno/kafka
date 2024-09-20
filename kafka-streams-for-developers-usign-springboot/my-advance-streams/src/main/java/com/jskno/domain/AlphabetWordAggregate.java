package com.jskno.domain;

import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record AlphabetWordAggregate(
    String key,
    Set<String> values,
    int runningCount) {

    public AlphabetWordAggregate() {
        this("", new HashSet<>(), 0);
    }

    public AlphabetWordAggregate updateNewEvents(String key, String newValue) {
        log.info("Before the update: {}", this);
        log.info("New Record -> key: {}, vale: {}", key, newValue);
        var newRunningCount = this.runningCount + 1;
        values.add(newValue);
        var aggregated = new AlphabetWordAggregate(key, values, newRunningCount);
        log.info("Aggregated: {}", aggregated);
        return aggregated;
    }
}
