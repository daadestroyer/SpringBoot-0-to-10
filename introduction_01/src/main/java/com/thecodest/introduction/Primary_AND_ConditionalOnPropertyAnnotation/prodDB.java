package com.thecodest.introduction.Primary_AND_ConditionalOnPropertyAnnotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "deploy.env",havingValue = "prod")
public class prodDB implements DB{
    @Override
    public String getData() {
        return "Using PROD DB";
    }
}



