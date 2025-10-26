package com.thecodest.introduction.Primary_AND_ConditionalOnPropertyAnnotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "deploy.env",havingValue = "dev")
public class devDB implements DB {
    @Override
    public String getData() {
        return "Using DEV DB";
    }
}

