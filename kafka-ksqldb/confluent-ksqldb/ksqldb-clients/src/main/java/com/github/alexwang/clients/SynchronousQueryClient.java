package com.github.alexwang.clients;

import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import io.confluent.ksql.api.client.Row;
import io.confluent.ksql.api.client.StreamedQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class SynchronousQueryClient {
    private static final Logger LOG = LoggerFactory.getLogger(SynchronousQueryClient.class);
    private static final String KSQLDB_SERVER_HOST = "192.168.88.130";
    private static final int KSQLDB_SERVER_PORT = 8088;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ClientOptions clientOptions = ClientOptions.create().setHost(KSQLDB_SERVER_HOST)
                .setPort(KSQLDB_SERVER_PORT);
        Client client = Client.create(clientOptions);
        StreamedQueryResult streamedQueryResult = client.streamQuery("select * from user emit changes;",
                ImmutableMap.of("auto.offset.reset", "earliest")).get();
        final AtomicBoolean start = new AtomicBoolean(true);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            start.set(false);
        }));
        while (start.get()) {
            Row row = streamedQueryResult.poll();
            LOG.info("{}", row);
/*            row.getInteger("ID");
            row.getInteger(1);*/
        }
        client.close();
    }
}
