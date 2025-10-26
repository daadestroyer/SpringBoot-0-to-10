package com.thecodest.introduction.QualifierAnnotation;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ShoppingCart {

    @Qualifier("offlineOrder")
    @Autowired
    Order order;

    public ShoppingCart() {
        System.out.println("Shopping Cart Initialized");
    }

    @PostConstruct
    public void shoppingStarted() {
        System.out.println("I am doing shopping");
    }

    public void shoppingDetails() {
        order.getDetails();
    }

    @PreDestroy
    public void completingShopping() {
        System.out.println("I am completing Shopping Now");
    }

}
