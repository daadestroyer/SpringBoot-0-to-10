package com.example.spring_profiles_08.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @GetMapping("/")
    public ResponseEntity<?> handleHealthCheckController() {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
