package com.example.CachingApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class CachingAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(CachingAppApplication.class, args);
    }

}
