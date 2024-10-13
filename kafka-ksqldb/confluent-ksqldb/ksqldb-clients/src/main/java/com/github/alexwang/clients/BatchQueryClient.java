package com.github.alexwang.clients;

import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import io.confluent.ksql.api.client.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class BatchQueryClient {
    private static final Logger LOG = LoggerFactory.getLogger(BatchQueryClient.class);
    private static final String KSQLDB_SERVER_HOST = "192.168.88.130";
    private static final int KSQLDB_SERVER_PORT = 8088;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ClientOptions clientOptions = ClientOptions.create().setHost(KSQLDB_SERVER_HOST)
                .setPort(KSQLDB_SERVER_PORT);
        Client client = Client.create(clientOptions);
        List<Row> result = client.executeQuery("select * from user;",
                ImmutableMap.of("auto.offset.reset", "earliest")).get();
        result.forEach(e -> LOG.info("{}", e));
        client.close();
    }
}
