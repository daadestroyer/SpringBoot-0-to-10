package com.thecodest.introduction.BeanLifeCycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
public class Product {
    @Autowired
    Category category;

    public Product() {
        System.out.println("Initializing Product");
    }
    @PostConstruct
    public void afterInitialize(){
        // here you can perform any task after bean construction and injection
        System.out.println("Bean is constructed and dependency is injected");
    }
    public void purchaseProduct(){
        System.out.println("I am purchasing product");
    }
    @PreDestroy
    public void preDestroy(){
        // here you can perform any task before bean get destroyed
        System.out.println("Bean is about to destroy");
    }
}
