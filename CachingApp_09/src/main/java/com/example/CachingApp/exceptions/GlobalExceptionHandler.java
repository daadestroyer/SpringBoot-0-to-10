package com.example.CachingApp.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ConfigDataResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ConfigDataResourceNotFoundException ex) {
        log.error(ex.getLocalizedMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(StaleObjectStateException.class)
    public ResponseEntity<?> handleStaleObjectState(StaleObjectStateException ex) {
        log.error(ex.getLocalizedMessage());
        return new ResponseEntity<>("Stale data\n", HttpStatus.CONFLICT);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> hanleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error(ex.getLocalizedMessage());
        return ResponseEntity.notFound().build();
    }
}
