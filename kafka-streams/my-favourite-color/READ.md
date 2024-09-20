
To test the application

1. Start KAFKA

   a) Start kafka in kraft mode (UBUNTU)
   cd ~/kafka_2.13-3.5.0
   bin/kafka-storage.sh random-uuid
   bin/kafka-storage.sh format -t <uuid> -c config/kraft/server.properties
   bin/kafka-server-start.sh config/kraft/server.properties

2. Create the initial, intermediate and final topics using kafka-topics
    cd c:/repo/kafka_2.13-3.4.0
    
    bin/kafka-topics.sh --create --replication-factor 1 --partitions 1 --topic favourite-colour-input --bootstrap-server localhost:9092
    bin/kafka-topics.sh --create --replication-factor 1 --partitions 1 --topic user-keys-and-colours --config cleanup.policy=compact --bootstrap-server localhost:9092
    bin/kafka-topics.sh --create --replication-factor 1 --partitions 1 --topic favourite-colour-output --config cleanup.policy=compact --bootstrap-server localhost:9092

    bin\kafka-topics.sh --list --bootstrap-server localhost:9092

3. Launch a Kafka consumer on the final topic

   bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
   --topic favourite-colour-output \
   --from-beginning \
   --formatter kafka.tools.DefaultMessageFormatter \
   --property print.key=true \
   --property print.value=true \
   --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
   --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

4. Let's run our application from Intellij
   Play in main method

5. Let's start a kafka producer in the input topic
   bin/kafka-console-producer.sh --broker-list localhost:9092 --topic favourite-colour-input

6. Write some data in it
    stephane,blue
    john,green
    stephane,red
    alice,red

7. Verify the data has been written to the final topic and how the result is evolving

8. Also debug the app in Intellij

9. List of topics to observe internal topics
   bin/kafka-topics.sh --list --bootstrap-server localhost:9092