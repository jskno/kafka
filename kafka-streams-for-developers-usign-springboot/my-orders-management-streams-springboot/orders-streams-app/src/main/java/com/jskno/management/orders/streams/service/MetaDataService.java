package com.jskno.management.orders.streams.service;

import com.jskno.management.orders.domain.dto.HostInfoDTO;
import com.jskno.management.orders.domain.dto.HostInfoWithKeyDTO;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyQueryMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;

@Service
public class MetaDataService {

    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;
    @Getter
    private final int currentMachinePort;

    public MetaDataService(StreamsBuilderFactoryBean streamsBuilderFactoryBean,
        @Value("${server.port}") int currentMachinePort) {
        this.streamsBuilderFactoryBean = streamsBuilderFactoryBean;
        this.currentMachinePort = currentMachinePort;
    }

    public List<HostInfoDTO> getOthersHostStreamsMetadata() {
        try {
            String currentMachineAddress = InetAddress.getLocalHost().getHostAddress();
            List<HostInfoDTO> streamsMetadata = getStreamsMetadata();
            return streamsMetadata.stream()
                .filter(hostInfoDTO -> //!currentMachineAddress.equals(hostInfoDTO.hostName()) &&
                    hostInfoDTO.port() != currentMachinePort)
                .toList();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public List<HostInfoDTO> getStreamsMetadata() {
        return Objects.requireNonNull(streamsBuilderFactoryBean.getKafkaStreams())
            .metadataForAllStreamsClients()
            .stream()
            .map(streamsMetadata -> new HostInfoDTO(streamsMetadata.hostInfo().host(), streamsMetadata.hostInfo().port()))
            .toList();
    }

    public Optional<HostInfoWithKeyDTO> getStreamsMetaData(String storeName, String locationId) {
        KeyQueryMetadata keyQueryMetadata = Objects.requireNonNull(streamsBuilderFactoryBean.getKafkaStreams())
            .queryMetadataForKey(storeName, locationId, Serdes.String().serializer());

        if (keyQueryMetadata != null) {
            return Optional.of(new HostInfoWithKeyDTO(
                keyQueryMetadata.activeHost().host(),
                keyQueryMetadata.activeHost().port(),
                locationId));
        }
        return Optional.empty();
    }



}
