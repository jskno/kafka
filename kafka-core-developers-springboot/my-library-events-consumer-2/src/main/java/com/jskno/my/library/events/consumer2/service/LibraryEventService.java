package com.jskno.my.library.events.consumer2.service;

import com.jskno.my.library.events.consumer2.domain.BookDTO;
import com.jskno.my.library.events.consumer2.domain.LibraryEventDTO;
import com.jskno.my.library.events.consumer2.domain.NewOperation;
import com.jskno.my.library.events.consumer2.domain.UpdateOperation;
import com.jskno.my.library.events.consumer2.exceptions.LibraryEventConsumerException;
import com.jskno.my.library.events.consumer2.exceptions.LibraryEventRecoverableException;
import com.jskno.my.library.events.consumer2.mappers.LibraryEventMapper;
import com.jskno.my.library.events.consumer2.repository.LibraryEventRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.ObjectError;

@Service
@Slf4j
@RequiredArgsConstructor
public class LibraryEventService {

    private final LibraryEventRepository libraryEventRepository;
    private final LibraryEventMapper libraryEventMapper;
    private final Validator validator;
    private final org.springframework.validation.Validator springValidator;

    public void processLibraryEvent(LibraryEventDTO libraryEventDTO) {
        switch (libraryEventDTO.type()) {
            case NEW:
                validateNew(libraryEventDTO);
                var newLibraryEvent = libraryEventMapper.mapToEntity(libraryEventDTO);
                libraryEventRepository.save(newLibraryEvent);
                break;
            case UPDATE:
                validateUpdate(libraryEventDTO);
                var updateLibraryEvent = libraryEventMapper.mapToEntity(libraryEventDTO);
                libraryEventRepository.save(updateLibraryEvent);
                break;
            default:
                log.info("Invalid library event type: {}", libraryEventDTO.type().name());
        }
    }

    public void validate(LibraryEventDTO libraryEventDTO) {
        var errors = springValidator.validateObject(libraryEventDTO);
        if (errors.hasErrors()) {
            var errorsMessage = errors.getAllErrors().stream().map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(". ")).trim();
            throw new LibraryEventConsumerException(errorsMessage);
        }
    }

    public void validateNew(LibraryEventDTO libraryEventDTO) {
        Set<ConstraintViolation<LibraryEventDTO>> violations = validator.validate(libraryEventDTO, NewOperation.class);
        Set<ConstraintViolation<BookDTO>> bookViolations = validator.validate(libraryEventDTO.book(), NewOperation.class);
        if (!CollectionUtils.isEmpty(violations) || !CollectionUtils.isEmpty(bookViolations)) {
            var libraryMessage = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(". "))
                .trim();
            var bookMessage = bookViolations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(". "))
                .trim();
            throw new LibraryEventConsumerException(libraryMessage + "." + bookMessage);
        }
    }

    public void validateUpdate(LibraryEventDTO libraryEventDTO) {
        Set<ConstraintViolation<LibraryEventDTO>> violations = validator.validate(libraryEventDTO, UpdateOperation.class);
        if (!CollectionUtils.isEmpty(violations)) {
            var errorsMessage = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(". ")).trim();
            throw new LibraryEventConsumerException(errorsMessage);
        }

        if (libraryEventDTO.id().equals(666L)) {
            throw new LibraryEventRecoverableException("DDBB is down at the moment for eventId: " + libraryEventDTO.id());
        }

        libraryEventRepository.findById(libraryEventDTO.id())
            .orElseThrow(() -> new LibraryEventConsumerException("Library Event with id " + libraryEventDTO.id() + " not found"));
    }

}
