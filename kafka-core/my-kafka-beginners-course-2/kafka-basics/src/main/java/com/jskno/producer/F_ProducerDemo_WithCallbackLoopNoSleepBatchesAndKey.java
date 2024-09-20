package com.jskno.producer;

import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

import java.util.Properties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class F_ProducerDemo_WithCallbackLoopNoSleepBatchesAndKey {

    public static final Logger log = LoggerFactory.getLogger(F_ProducerDemo_WithCallbackLoopNoSleepBatchesAndKey.class.getSimpleName());
    public static final String WORD_SEPARATOR = "---";



    public static void main(String[] args) {

        Properties properties = new Properties();
        properties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        String topic = "demo-java";

        for (int j = 0; j < 2; j++) {

            for(int i = 0; i < 10; i++) {

                // create a producer record
                String key = "Id_" + i;
                String value = "Record" + i;
                ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, value);

                producer.send(producerRecord, (metadata, ex) -> {
                    if (ex == null) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("Received new metadata").append(WORD_SEPARATOR);
                        builder.append("Topic: ").append(metadata.topic()).append(WORD_SEPARATOR);
                        builder.append("Key: ").append(producerRecord.key()).append(WORD_SEPARATOR);
                        builder.append("Partition: ").append(metadata.partition()).append(WORD_SEPARATOR);
                        builder.append("Offset: ").append(metadata.offset()).append(WORD_SEPARATOR);
                        builder.append("Timestamp: ").append(metadata.timestamp()).append(WORD_SEPARATOR);
                        log.info(builder.toString());
                    } else {
                        log.error("Error while producing", ex);
                    }
                });

            }

        }

        producer.flush();
        producer.close();

        // The messages will go to the same partition in groups where the hash key value is the same.
        // Producer is using StickyPartitioner and as we are sending 20 messages quite fast, the producer will group them
        // according to its key hash and send them together to increase performance (batching)


    }

}
