package com.example.spring_profiles_08;

import com.example.spring_profiles_08.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringProfiles08Application implements CommandLineRunner {

    @Autowired
    private DataService dataService;

    @Value("${my.variable}")
    private String myVariable;

    public static void main(String[] args) {
        SpringApplication.run(SpringProfiles08Application.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("My variable = " + myVariable);
        System.out.println(dataService.getData());

    }
}
