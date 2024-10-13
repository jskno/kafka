package com.github.alexwang.kafka.streams.stateful;

import com.github.alexwang.kafka.streams.model.Transaction;
import com.github.alexwang.kafka.streams.model.TransactionKey;
import com.github.alexwang.kafka.streams.model.TransactionPattern;
import com.github.alexwang.kafka.streams.model.TransactionReward;
import com.github.alexwang.kafka.streams.serdes.JsonSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class XMallStatefulTransactionApp {
    private final static Logger LOG = LoggerFactory.getLogger(XMallStatefulTransactionApp.class);
    private final static String APP_ID = "xmall_stateful_app2";
    private final static String BOOTSTRAP_SERVER = "192.168.88.130:9092";
    private final static String XMALL_TRANSACTION_SOURCE_TOPIC = "xmall.transaction";
    private final static String XMALL_TRANSACTION_PATTERN_TOPIC = "xmall.pattern.transaction";
    private final static String XMALL_TRANSACTION_REWARDS_TOPIC = "xmall.rewards.transaction";
    private final static String XMALL_TRANSACTION_PURCHASES_TOPIC = "xmall.purchases.transaction";
    private final static String XMALL_TRANSACTION_COFFEE_TOPIC = "xmall.coffee.transaction";
    private final static String XMALL_TRANSACTION_ELECT_TOPIC = "xmall.elect.transaction";

    private final static String STATE_STORE_NAME = "xmall_stateful_state_store";

    public static void main(String[] args) throws InterruptedException {
        Properties conf = new Properties();
        conf.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        conf.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);
        conf.put(StreamsConfig.STATE_DIR_CONFIG, "C:\\WorkBench\\Work\\kafka\\statestore");
        conf.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        StreamsBuilder builder = new StreamsBuilder();

        //1. consume the raw data from source topic `transaction`
        KStream<String, Transaction> ks0 = builder.stream(XMALL_TRANSACTION_SOURCE_TOPIC,
                Consumed.with(Serdes.String(), JsonSerdes.Transaction()).withName("transaction-source")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));

        //2. masking the transaction records, just for credit card number
        KStream<String, Transaction> ks1 = ks0.peek((k, v) -> LOG.info("pre masking:{}", v))
                .mapValues(v -> Transaction.newBuilder(v).maskCreditCard().build(), Named.as("transaction-masking-pii"));

        //3. extract the pattern data from transaction
        ks1.mapValues(v -> TransactionPattern.builder(v).build(), Named.as("transaction-pattern"))
                .to(XMALL_TRANSACTION_PATTERN_TOPIC, Produced.with(Serdes.String(), JsonSerdes.TransactionPattern()));

        //4. process the customer reward
        ks1.mapValues(v -> TransactionReward.builder(v).build(), Named.as("transaction-reward"))
                .selectKey((k, v) -> v.getCustomerId())
                .repartition(Repartitioned.with(Serdes.String(), JsonSerdes.TransactionReward()))
                .transformValues(new ValueTransformerSupplier<TransactionReward, TransactionReward>() {
                    @Override
                    public ValueTransformer<TransactionReward, TransactionReward> get() {
                        return new ValueTransformer<TransactionReward, TransactionReward>() {
                            private KeyValueStore<String, Integer> keyValueStore;

                            @Override
                            public void init(ProcessorContext context) {
                                this.keyValueStore = context.getStateStore(STATE_STORE_NAME);
                            }

                            @Override
                            public TransactionReward transform(TransactionReward reward) {
                                Integer totalRewardPoints = keyValueStore.get(reward.getCustomerId());
                                if (totalRewardPoints == null || totalRewardPoints == 0) {
                                    totalRewardPoints = reward.getRewardPoints();
                                } else {
                                    totalRewardPoints += reward.getRewardPoints();
                                }
                                keyValueStore.put(reward.getCustomerId(), totalRewardPoints);
                                TransactionReward newTransactionReward = TransactionReward.builder(reward).build();
                                newTransactionReward.setTotalPoints(totalRewardPoints);
                                return newTransactionReward;
                            }

                            @Override
                            public void close() {

                            }
                        };
                    }

                    @Override
                    public Set<StoreBuilder<?>> stores() {
                        StoreBuilder<KeyValueStore<String, Integer>> keyValueStoreBuilder = Stores.keyValueStoreBuilder(
                                Stores.persistentKeyValueStore(STATE_STORE_NAME), Serdes.String(), Serdes.Integer()
                        );
                        return Collections.singleton(keyValueStoreBuilder);
                    }
                }, Named.as("total-reward-points-processor"))
                .to(XMALL_TRANSACTION_REWARDS_TOPIC, Produced.with(Serdes.String(), JsonSerdes.TransactionReward()));

        //5. process the purchase
        ks1.filter((k, v) -> v.getPrice() > 5.0D)
                .selectKey((k, v) -> new TransactionKey(v.getCustomerId(), v.getPurchaseDate()))
                .to(XMALL_TRANSACTION_PURCHASES_TOPIC, Produced.with(JsonSerdes.TransactionKey(), JsonSerdes.Transaction()));

        //6. split the coffee and elect
        ks1.split(Named.as("transaction-split"))
                .branch((k, v) -> v.getDepartment().equals("COFFEE"),
                        Branched.withConsumer(ks -> ks.to(XMALL_TRANSACTION_COFFEE_TOPIC, Produced.with(Serdes.String(), JsonSerdes.Transaction()))))
                .branch((k, v) -> v.getDepartment().equals("ELECT"),
                        Branched.withConsumer(ks -> ks.to(XMALL_TRANSACTION_ELECT_TOPIC, Produced.with(Serdes.String(), JsonSerdes.Transaction()))));
        //7. locate the data into data lake
        ks1.foreach((k, v) -> LOG.info("simulate located the transaction record(masked) to the data lake, the value:{}", v));

        Topology topology = builder.build();
        KafkaStreams kafkaStreams = new KafkaStreams(topology, conf);
        final CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            kafkaStreams.close();
            latch.countDown();
            LOG.info("The kafka streams application is closed.");
        }));
        kafkaStreams.start();
        LOG.info("The kafka streams application start...");
        latch.await();
    }
}
