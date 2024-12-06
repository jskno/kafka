package com.jskno.email.notification;

import com.jskno.email.notification.entity.ProcessEventEntity;
import com.jskno.email.notification.exception.NotRetryableException;
import com.jskno.email.notification.repository.ProcessEventRepository;
import com.jskno.kafka.event.driven.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessEventService {

    private final ProcessEventRepository processEventRepository;

    public void storeProductEvent(ProductCreatedEvent event, String messageId) {
        try {
            processEventRepository.save(ProcessEventEntity.builder()
                    .messageId(messageId)
                    .productId(event.getId())
                    .build());
        } catch (DataIntegrityViolationException ex) {
            throw new NotRetryableException(ex);
        }
    }
}
