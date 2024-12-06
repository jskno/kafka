package com.jskno.email.notification.repository;

import com.jskno.email.notification.entity.ProcessEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcessEventRepository extends JpaRepository<ProcessEventEntity, Long> {

    Optional<ProcessEventEntity> findByMessageId(String messageId);
}
