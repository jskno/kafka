package com.jskno.topology;

import com.jskno.domain.Greeting;
import com.jskno.serdes.generics.SerdesFactory;
import com.jskno.serdes.harcoded.GreetingSerdes;
import com.jskno.topology.kstream.GreetingsTopologyV8;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GreetingsTopologyV8Test {

    TopologyTestDriver topologyTestDriver;
    TestInputTopic<String, Greeting> inputTopic;
    TestOutputTopic<String, Greeting> outputTopic;

    @BeforeEach
    void setUp() {
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "greetings-app");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GreetingSerdes.class);
        topologyTestDriver = new TopologyTestDriver(GreetingsTopologyV8.buildTopology(), properties);

        inputTopic = topologyTestDriver.createInputTopic(
            GreetingsTopologyV8.GREETINGS,
            Serdes.String().serializer(),
            SerdesFactory.jsonSerdes(Greeting.class).serializer());

        outputTopic = topologyTestDriver.createOutputTopic(
            GreetingsTopologyV8.GREETINGS_UPPERCASE,
            Serdes.String().deserializer(),
            SerdesFactory.jsonSerdes(Greeting.class).deserializer());
    }

    @AfterEach
    void tearDown() {
        topologyTestDriver.close();
    }

    @Test
    void buildTopology() {
        // Given
        inputTopic.pipeInput("GM", new Greeting("Good Morning!", OffsetDateTime.now()));

        // When

        // Then
        long count = outputTopic.getQueueSize();
        Assertions.assertEquals(1, count);
        KeyValue<String, Greeting> outputValue = outputTopic.readKeyValue();
        Assertions.assertEquals("GM", outputValue.key);
        Assertions.assertEquals("GOOD MORNING!", outputValue.value.message());
        Assertions.assertNotNull(outputValue.value.timeStamp());
    }

    @Test
    void buildTopology_WhenMultipleInputs() {
        // Given
        inputTopic.pipeKeyValueList(List.of(
            KeyValue.pair("GM", new Greeting("Good Morning!", OffsetDateTime.now())),
            KeyValue.pair("GE", new Greeting("Good Evening!", OffsetDateTime.now())
        )));

        // When

        // Then
        long count = outputTopic.getQueueSize();
        Assertions.assertEquals(2, count);

        List<KeyValue<String, Greeting>> outputValues = outputTopic.readKeyValuesToList();
        Assertions.assertEquals(2, outputValues.size());

        KeyValue<String, Greeting> outputValue1 = outputValues.getFirst();
        Assertions.assertEquals("GM", outputValue1.key);
        Assertions.assertEquals("GOOD MORNING!", outputValue1.value.message());
        Assertions.assertNotNull(outputValue1.value.timeStamp());
        KeyValue<String, Greeting> outputValue2 = outputValues.getLast();
        Assertions.assertEquals("GE", outputValue2.key);
        Assertions.assertEquals("GOOD EVENING!", outputValue2.value.message());
        Assertions.assertNotNull(outputValue2.value.timeStamp());
    }

    @Test
    void buildTopology_WhenErrorScenario() {
        // Given
        inputTopic.pipeInput("TE", new Greeting("TransientError", OffsetDateTime.now()));

        // When

        // Then
        long count = outputTopic.getQueueSize();
        Assertions.assertEquals(0, count);
    }

}
