package com.jskno.management.orders.streams.controller;

import com.jskno.management.orders.domain.dto.HostInfoDTO;
import com.jskno.management.orders.streams.service.MetaDataService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/metadata")
@RequiredArgsConstructor
public class MetaDataController {

    private final MetaDataService metaDataService;

    @GetMapping("/all")
    public List<HostInfoDTO> getStreamsMetaData() {
        return metaDataService.getStreamsMetadata();
    }



}
