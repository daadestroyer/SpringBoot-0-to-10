package com.thecodest.introduction.Way2_Of_CreatingBean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public Banana getBananaBean(){
        return new Banana();
    }
    @Bean
    public Cake getCakeBean(){
        return new Cake();
    }
}
