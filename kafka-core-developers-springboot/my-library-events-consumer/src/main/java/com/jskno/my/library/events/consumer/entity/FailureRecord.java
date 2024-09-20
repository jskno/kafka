package com.jskno.my.library.events.consumer.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class FailureRecord {

    @Id
    @GeneratedValue
    private Integer id;

    private String topic;

    private Integer topicKey;

    private String errorRecord;

    private Integer partition;

    private Long offsetValue;

    private String exception;

    private String status;

}
