package com.thecodest.introduction;

import com.thecodest.introduction.BeanLifeCycle.Product;
import com.thecodest.introduction.BeanScope.User;
import com.thecodest.introduction.Primary_AND_ConditionalOnPropertyAnnotation.DB;
import com.thecodest.introduction.QualifierAnnotation.Order;
import com.thecodest.introduction.QualifierAnnotation.ShoppingCart;
import com.thecodest.introduction.Way1_Of_Creating_Bean.Apple;
import com.thecodest.introduction.Way2_Of_CreatingBean.Banana;
import com.thecodest.introduction.Way2_Of_CreatingBean.Cake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IntroductionApplication implements CommandLineRunner {

	@Autowired
	Apple newApple;
	@Autowired
	Banana banana;
	@Autowired
	Cake cake;

	@Autowired
	Product product;

	@Autowired
	ShoppingCart shoppingCart;

	@Autowired
	User user1;
	@Autowired
	User user2;

	@Autowired
	DB db;

	public static void main(String[] args) {
		SpringApplication.run(IntroductionApplication.class, args);
	}
	@Override
	public void run(String... args) throws Exception {

		// WAY - 1 : FOR CREATING BEAN

		// this we are doing previously without Spring Bean
		// Apple apple = new Apple();
		// apple.eatApple();

		// now we don't need to create object Spring Bean will create automatically
		// to do this
		// we need to declare Apple reference variable and add @Autowired annotation so that Spring IOC container inject it automatically
		// Also we need to mark Apple class with @Component annotation so that IOC Container get to know about this class
		// and IOC Container create object of Apple and inject here in our class

		 newApple.eatApple();

		// WAY - 2 : FOR CREATING BEAN
		 banana.eatBanana();
		 cake.eatCake("Chocolate");

		// Spring Lifecycle
		 product.purchaseProduct();

		// @Qualifier Annotation
		shoppingCart.shoppingDetails();

		// Bean Scope is singleton so hashcode will be same
		// Bean Scope is prorotype so hashcode for all object will be different
		user1.howAreYou();
		user2.howAreYou();
		System.out.println("User1 hashcode = "+user1.hashCode());
		System.out.println("User2 hashcode = "+user2.hashCode());

		// @Primary and @ConditionalOnProperty
		System.out.println(db.getData());
	}
}
