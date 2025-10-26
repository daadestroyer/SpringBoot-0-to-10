package com.thecodest.introduction.BeanLifeCycle;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class Category {
    public Category() {
        System.out.println("Initializing Category");
    }
}
