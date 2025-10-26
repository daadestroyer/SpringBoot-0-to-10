package com.thecodest.introduction.QualifierAnnotation;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("onlineOrder")
public class OnlineOrder implements Order{
    @Override
    public void getDetails() {
        System.out.println("I have done online shopping ");
    }
}
