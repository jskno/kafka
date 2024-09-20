package com.jskno.my.library.events.consumer.jpa;

import com.jskno.my.library.events.consumer.entity.FailureRecord;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface FailureRecordRepository extends CrudRepository<FailureRecord, Integer> {

    List<FailureRecord> findAllByStatus(String retry);
}
