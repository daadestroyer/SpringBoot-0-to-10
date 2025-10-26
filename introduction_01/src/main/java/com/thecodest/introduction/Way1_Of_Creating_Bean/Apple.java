package com.thecodest.introduction.Way1_Of_Creating_Bean;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component // IOC Container will get to know about this class and create Object of this accordingly
@Service
public class Apple {
    public void eatApple(){
        System.out.println("I am eating apple :-)");
    }
}
