package com.github.alexwang.clients;

import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class ListStreamClient {
    private static final Logger LOG = LoggerFactory.getLogger(ListStreamClient.class);
    private static final String KSQLDB_SERVER_HOST = "192.168.88.130";
    private static final int KSQLDB_SERVER_PORT = 8088;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ClientOptions clientOptions = ClientOptions.create().setHost(KSQLDB_SERVER_HOST)
                .setPort(KSQLDB_SERVER_PORT);
        Client client = Client.create(clientOptions);
        client.listStreams().get().forEach(streamInfo -> LOG.info(streamInfo.toString()));
        client.close();
    }
}
