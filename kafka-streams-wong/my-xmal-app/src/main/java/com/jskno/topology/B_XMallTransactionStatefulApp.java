package com.jskno.topology;

import com.jskno.model.CustomerReward;
import com.jskno.model.Transaction;
import com.jskno.model.TransactionKey;
import com.jskno.model.TransactionPattern;
import com.jskno.serdes.JsonSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.api.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;


// sudo ./bin/kafka-server-start.sh config/kraft/server.properties
// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic xmall.transaction --partitions 3 --replication-factor 1
// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic xmall.reward.transaction --partitions 3 --replication-factor 1
// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic xmall.purchases.transaction --partitions 3 --replication-factor 1
// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic xmall.pattern.transaction --partitions 3 --replication-factor 1
// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic xmall.elect.transaction --partitions 3 --replication-factor 1
// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic xmall.coffee.transaction --partitions 3 --replication-factor 1
// ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic xmall.transaction
// ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic xmall.reward.transaction --from-beginning
// ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic xmall.purchases.transaction --from-beginning
// ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic xmall.pattern.transaction --from-beginning
// ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic xmall.elect.transaction --from-beginning
// ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic xmall.coffee.transaction --from-beginning
public class B_XMallTransactionStatefulApp {

    private final static Logger LOGGER = LoggerFactory.getLogger(B_XMallTransactionStatefulApp.class);
    private final static String STORES_NAME = "transaction-store";

    public static void main(String[] args) throws InterruptedException {
        Properties props = buildStreamsProperties();
        Topology topology = buildTopology();

        KafkaStreams streams = new KafkaStreams(topology, props);

        CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            streams.close();
            latch.countDown();
            LOGGER.info("The Transaction Processor App is gracefully shutting down");
        }));

        streams.start();
        LOGGER.info("Transaction Processor App is started");

        latch.await();

    }

    private static Properties buildStreamsProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "transactions-stateful");
        return props;
    }

    private static Topology buildTopology() {
        StoreBuilder<KeyValueStore<String, Integer>> keyValueStoreBuilder = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(STORES_NAME), Serdes.String(), Serdes.Integer());
        StreamsBuilder builder = new StreamsBuilder();
        builder.addStateStore(keyValueStoreBuilder);

        KStream<String, Transaction> transactionSource = builder.stream(
                "xmall.transaction",
                Consumed.with(Serdes.String(), JsonSerdes.of(Transaction.class)).
                        withName("transaction-source")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));

        KStream<String, Transaction> transactionMasked = transactionSource
                .mapValues(Transaction::fromMaskingCreditCard, Named.as("transaction-masking-pii"));

        transactionMasked.mapValues(v -> TransactionPattern.builder()
                        .zipCode(v.zipCode())
                        .item(v.itemPurchased())
                        .date(v.purchasedDate())
                        .amount(v.price().multiply(BigDecimal.valueOf(v.quantity())))
                        .build(), Named.as("transaction-pattern"))
                .to("xmall.pattern.transaction", Produced.with(Serdes.String(), JsonSerdes.of(TransactionPattern.class)));

        transactionMasked.mapValues(v -> CustomerReward.builder()
                        .customerId(v.customerId())
                        .purchaseTotal(v.price().multiply(BigDecimal.valueOf(v.quantity())))
                        .rewardPoints(v.price().multiply(BigDecimal.valueOf(v.quantity())).intValue())
                        .build(), Named.as("customer-reward"))
                .selectKey((k, v) -> v.customerId())
                .repartition(Repartitioned.with(Serdes.String(), JsonSerdes.of(CustomerReward.class)))
                .processValues(() -> new FixedKeyProcessor<String, CustomerReward, CustomerReward>() {

                    private KeyValueStore<String, Integer> keyValueStore;
                    private FixedKeyProcessorContext<String, CustomerReward> context;

                    @Override
                    public void init(FixedKeyProcessorContext<String, CustomerReward> context) {
                        this.context = context;
                        keyValueStore = context.getStateStore(STORES_NAME);
                    }

                    @Override
                    public void process(FixedKeyRecord<String, CustomerReward> fixedKeyRecord) {
                        CustomerReward reward = fixedKeyRecord.value();
                        Integer totalRewardPoints = keyValueStore.get(fixedKeyRecord.key());
                        if (totalRewardPoints == null || totalRewardPoints == 0) {
                            totalRewardPoints = reward.rewardPoints();
                        } else {
                            totalRewardPoints += reward.rewardPoints();
                        }
                        keyValueStore.put(fixedKeyRecord.key(), totalRewardPoints);

                        // Create a new TransactionReward with updated total points
                        CustomerReward newTransactionReward = CustomerReward.builder()
                                .customerId(reward.customerId())
                                .purchaseTotal(reward.purchaseTotal())
                                .rewardPoints(reward.rewardPoints())
                                .totalPoints(totalRewardPoints)
                                .build();

                        // Forward the updated record
                        context.forward(fixedKeyRecord.withValue(newTransactionReward));
                    }

                    @Override
                    public void close() {
                        // No specific cleanup required
                    }
                }, Named.as("total-reward-points"), STORES_NAME)
                .to("xmall.reward.transaction", Produced.with(Serdes.String(), JsonSerdes.of(CustomerReward.class)));

        transactionMasked
                .filter((k, v) -> v.price().doubleValue() > 5)
                .selectKey((k, v) -> TransactionKey.builder()
                        .customerId(v.customerId())
                        .transactionDate(v.purchasedDate())
                        .build())
                .to("xmall.purchases.transaction", Produced.with(JsonSerdes.of(TransactionKey.class), JsonSerdes.of(Transaction.class)));

        transactionMasked
                .split(Named.as("transaction-split-"))
                .branch((k, v) -> v.department().equals("coffee"),
                        Branched.withConsumer(ks -> ks.to("xmall.coffee.transaction",
                                Produced.with(Serdes.String(), JsonSerdes.of(Transaction.class)))))
                .branch((s, transaction) -> transaction.department().equals("elect"),
                        Branched.withConsumer(ks -> ks.to("xmall.elect.transaction",
                                Produced.with(Serdes.String(), JsonSerdes.of(Transaction.class)))));

        transactionMasked.foreach((k, v) ->
                LOGGER.debug("Simulate located the transaction record(masked) to the data lake, the value: {}", v));

        return builder.build();
    }
}
