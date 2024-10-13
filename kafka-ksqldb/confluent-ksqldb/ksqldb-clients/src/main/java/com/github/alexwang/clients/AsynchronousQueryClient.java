package com.github.alexwang.clients;

import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import io.confluent.ksql.api.client.Row;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class AsynchronousQueryClient {
    private static final Logger LOG = LoggerFactory.getLogger(AsynchronousQueryClient.class);
    private static final String KSQLDB_SERVER_HOST = "192.168.88.130";
    private static final int KSQLDB_SERVER_PORT = 8088;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ClientOptions clientOptions = ClientOptions.create().setHost(KSQLDB_SERVER_HOST)
                .setPort(KSQLDB_SERVER_PORT);
        Client client = Client.create(clientOptions);
        client.streamQuery("select * from user emit changes;",
                        ImmutableMap.of("auto.offset.reset", "earliest"))
                .thenAccept(streamedQueryResult -> {
                    LOG.info("Query has started,Query ID:{}", streamedQueryResult.queryID());
                    streamedQueryResult.subscribe(new RowSubscriber());
                }).exceptionally(e -> {
                    LOG.error("error", e);
                    return null;
                });

        final CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            client.close();
            latch.countDown();
        }));
        latch.await();
    }

    private static class RowSubscriber implements Subscriber<Row> {

        private Subscription subscription;

        @Override
        public synchronized void onSubscribe(Subscription subscription) {
            LOG.info("subscribed.");
            this.subscription = subscription;
            subscription.request(1);
        }

        @Override
        public synchronized void onNext(Row row) {
            LOG.info("Received row:{}", row);
            subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {
            LOG.error("error", throwable);
        }

        @Override
        public void onComplete() {
            LOG.info("Completed");
        }
    }
}
