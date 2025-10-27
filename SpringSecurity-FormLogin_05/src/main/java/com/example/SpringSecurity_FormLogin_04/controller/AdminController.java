package com.example.SpringSecurity_FormLogin_04.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    @GetMapping
    public ResponseEntity<?> adminAPI() {
        return new ResponseEntity<>("this is admin api", HttpStatus.OK);
    }

}
