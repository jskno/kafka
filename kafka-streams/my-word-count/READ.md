
To test the application

1. Start KAFKA
   a) Start kafka in kraft mode (UBUNTU)
    cd ~/kafka_2.13-3.5.0
    bin/kafka-storage.sh random-uuid
    bin/kafka-storage.sh format -t <uuid> -c config/kraft/server.properties
    bin/kafka-server-start.sh config/kraft/server.properties
    
   b) Start zookeeper and kafka
    cd c:/repo/kafka_2.13-3.4.0
    bin/windows/zookeeper-server-start.bat config/zookeeper.properties

    cd c:/repo/kafka_2.13-3.4.0
    bin/windows/kafka-server-start.bat config/server.properties

2. Create the initial and final topic using kafka-topics
    cd c:/repo/kafka_2.13-3.4.0
    bin/windows/kafka-topics.bat --create --replication-factor 1 --partition 1 --topic word-count-input
    bin/windows/kafka-topics.bat --create --replication-factor 1 --partition 1 --topic word-count-output

    bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092

3. Let's start a kafka producer in the input Topic
   bin\windows\kafka-console-producer.bat --topic word-count-input --bootstrap-server localhost:9092

4. Write some data in it
   kafka streams udemy
   kafka data processing
   kafka streams course

5. Verify the data has been written
   bin\windows\kafka-console-consumer.bat --topic word-count-input --from-beginning --bootstrap-server localhost:9092 

6. Stop that consumer and start a new one on the output topic
   bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 ^
       --topic word-count-output ^
       --from-beginning ^
       --formatter kafka.tools.DefaultMessageFormatter ^
       --property print.key=true ^
       --property print.value=true ^
       --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer ^
       --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

   bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
   --topic word-count-output \
   --from-beginning \
   --formatter kafka.tools.DefaultMessageFormatter \
   --property print.key=true \
   --property print.value=true \
   --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
   --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

7. Let's run our application from Intellij
    Play in main method

8. We can write some more data in the open console producer and see how the word counts evolve in the console consumer
9. Also debug the app in Intellij