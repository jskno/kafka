package com.jskno;

import org.apache.kafka.clients.admin.NewTopic;

public class StreamsUtils {

    public static final String PROPERTIES_FILE_PATH = "src/main/resources/streams.properties";
    public static final short REPLICATION_FACTOR = 3;
    public static final int PARTITIONS = 6;

    public static NewTopic createTopic(final String topicName){
        return new NewTopic(topicName, PARTITIONS, REPLICATION_FACTOR);
    }

}
