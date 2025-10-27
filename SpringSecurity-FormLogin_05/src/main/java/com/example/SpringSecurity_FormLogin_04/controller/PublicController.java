package com.example.SpringSecurity_FormLogin_04.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PublicController {

    @GetMapping
    public ResponseEntity<?> publicAPI() {
        return new ResponseEntity<>("this is public api", HttpStatus.OK);
    }
}
