package com.jskno.producer;

import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

import java.util.Properties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RoundRobinPartitioner;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class D_ProducerDemo_WithCallbackLoopForceSleep {

    public static final Logger log = LoggerFactory.getLogger(D_ProducerDemo_WithCallbackLoopForceSleep.class.getSimpleName());
    public static final String LINE_SEPARATOR = System.lineSeparator();

    public static void main(String[] args) {

        Properties properties = new Properties();
        properties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, "400");
//        properties.setProperty(ProducerConfig.PARTITIONER_CLASS_CONFIG, RoundRobinPartitioner.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        for (int j = 0; j < 10; j++) {

            for(int i = 0; i < 30; i++) {
                ProducerRecord<String, String> producerRecord = new ProducerRecord<>("demo-java", "record " + j + i);
                producer.send(producerRecord, (metadata, ex) -> {
                    if (ex == null) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("Received new metadata").append(LINE_SEPARATOR);
                        builder.append("Topic: ").append(metadata.topic()).append(LINE_SEPARATOR);
                        builder.append("Partition: ").append(metadata.partition()).append(LINE_SEPARATOR);
                        builder.append("Offset: ").append(metadata.offset()).append(LINE_SEPARATOR);
                        builder.append("Timestamp: ").append(metadata.timestamp()).append(LINE_SEPARATOR);
                        log.info(builder.toString());
                    } else {
                        log.error("Error while producing", ex);
                    }
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

        }

        producer.flush();
        producer.close();

        // The messages will not always go to the same partition. They are grouped and these element from the same
        // group will go to the same partition.
        // Producer is using StickyPartitioner but as we are sending many many messages quite slow , the producer will group them
        // and send them together to increase performance (batching) but in this case we can see how the partition changes
    }

}
