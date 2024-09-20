
* Test producer

Start Kakfa

    cd ~/kafka_2.13-3.5.0
    bin/kafka-server-start.sh config/kraft/server.properties

Create topic

    bin/kafka-topics.sh --create --topic bank-transactions --partitions 1 --replication-factor 1 --bootstrap-server localhost:9092

Start consumer to capture BankBalanceProducer events

    bin/kafka-console-consumer.sh --topic bank-transactions --formatter kafka.tools.DefaultMessageFormatter --property print.key=true --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer --bootstrap-server localhost:9092 --from-beginning

Start BankBalanceProducer (main method)

* Purge Topic

- To purge messages, we can temporarily reset the retention.ms topic-level property to ten seconds and wait for messages to expire:

    `bin/kafka-configs.sh --alter --add-config retention.ms=10000 --topic bank-transactions --bootstrap-server localhost:9092 && sleep 10`

- Next, let's verify that the messages have expired from the topic:

    `bin/kafka-console-consumer.sh --topic bank-transactions --max-messages 1 --timeout-ms 1000 --from-beginning --bootstrap-server localhost:9092`

- Finally, we can restore the original retention period of seven days for the topic:

    `bin/kafka-configs.sh --alter --add-config retention.ms=604800000 --topic bank-transactions --bootstrap-server localhost:9092`

