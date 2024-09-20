package com.jskno.topology;

import com.jskno.topology.ktable.ExploreKTableTopologyV4;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExploreKTableTopologyV4Test {

    TopologyTestDriver topologyTestDriver;
    TestInputTopic<String, String> wordsInputTopic;
    TestOutputTopic<String, String> wordsOutputTopic;

    @BeforeEach
    void setUp() {
        topologyTestDriver = new TopologyTestDriver(ExploreKTableTopologyV4.build());

        wordsInputTopic = topologyTestDriver.createInputTopic(
            ExploreKTableTopologyV4.WORDS,
            Serdes.String().serializer(),
            Serdes.String().serializer());

        wordsOutputTopic = topologyTestDriver.createOutputTopic(
            ExploreKTableTopologyV4.WORDS_OUTPUT,
            Serdes.String().deserializer(),
            Serdes.String().deserializer());
    }

    @AfterEach
    void tearDown() {
        topologyTestDriver.close();
    }

    @Test
    void test() {
        // Given
        wordsInputTopic.pipeInput("A", "Apple");
        wordsInputTopic.pipeInput("A", "Airplane");
        wordsInputTopic.pipeInput("B", "Bus");
        wordsInputTopic.pipeInput("B", "Baby");

        // When

        // Then
        long outputCount = wordsOutputTopic.getQueueSize();
        // There actually should be 2 outputs, but the test is unable to simulate the
        // catching mechanism of the KTable in which the only retain the last value for each key, so it will output 2
        Assertions.assertEquals(4, outputCount);

        System.out.println("Output values: " + wordsOutputTopic.readKeyValuesToList());
    }

}
