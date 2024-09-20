package com.jskno.producer;

import static com.jskno.constants.ApplicationConstants.LINE_SEPARATOR;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerDemoWithCallbackLoopForceSleepAndKeys {

    public static final Logger log = LoggerFactory.getLogger(ProducerDemoWithCallbackLoopForceSleepAndKeys.class.getSimpleName());

    public static void main(String[] args) {

        // Given
        // bin\windows\zookeeper-server-start.bat config\zookeeper.properties
        // bin\windows\kafka-server-start.bat config\server.properties
        // bin\windows\kafka-topics.bat --bootstrap-server 127.0.0.1:9092 --create --topic demo_java --partitions 3 --replication-factor 1
        // bin\windows\kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic demo_java
        log.info("I am a Kafka Producer with Callback !!");

        // create Producer Properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create the Producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        String topic = "demo_java";

        for (int i = 0; i < 10; i++) {

            // create a producer record
            String key = "Id_" + i;
            String value = "Record" + i;
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, value);

            // send data - asynchronous operation
            producer.send(producerRecord, (metadata, ex) -> {
                // executes every time a record is successfully sent or an exception is thrown
                if (ex == null) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Received new metadata").append(LINE_SEPARATOR);
                    builder.append("Topic: ").append(metadata.topic()).append(LINE_SEPARATOR);
                    builder.append("Key: ").append(producerRecord.key()).append(LINE_SEPARATOR);
                    builder.append("Partition: ").append(metadata.partition()).append(LINE_SEPARATOR);
                    builder.append("Offset: ").append(metadata.offset()).append(LINE_SEPARATOR);
                    builder.append("Timestamp: ").append(metadata.timestamp()).append(LINE_SEPARATOR);
                    log.info(builder.toString());
                } else {
                    log.error("Error while producing", ex);
                }
            });
        }

        // flush and close the Producer - synchronous
        producer.flush();
        producer.close();

        // All the messages will go to the same partition.
        // Producer is using StickyPartitioner because as we are sending 10 messages quite fast, the producer will group them
        // and send them together to increase performance (batching)

    }

}
