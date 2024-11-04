package com.linuxacademy.ccdak.testing;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author will
 */
public class MyConsumerTest {

    MockConsumer<Integer, String> mockConsumer;
    MyConsumer myConsumer;

    private ByteArrayOutputStream systemOutContent;
    private final PrintStream originalSystemOut = System.out;

    @Before
    public void setUp() {
        mockConsumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
        myConsumer = new MyConsumer();
        myConsumer.consumer = mockConsumer;
    }

    @Before
    public void setUpStreams() {
        systemOutContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(systemOutContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalSystemOut);
    }

    @Test
    public void testHandleRecordsOutput() {
        String topic = "test_topic";
        ConsumerRecord<Integer, String> record = new ConsumerRecord<>(topic, 0, 1, 2, "Test value");

        mockConsumer.assign(List.of(new TopicPartition(topic, 0)));
        HashMap<TopicPartition, Long> beginningOffsets = new HashMap<>();
        beginningOffsets.put(new TopicPartition(topic, 0), 0L);
        mockConsumer.updateBeginningOffsets(beginningOffsets);

        mockConsumer.addRecord(record);

        myConsumer.handleRecords();
        Assert.assertEquals("key=2, value=Test value, topic=test_topic, partition=0, offset=1\r\n", systemOutContent.toString());
    }

    
}
