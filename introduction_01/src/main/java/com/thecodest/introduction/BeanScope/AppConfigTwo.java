package com.thecodest.introduction.BeanScope;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppConfigTwo{
    @Bean
    @Scope("prototype")
    public User getUserBean(){
        return new User();
    }
}
