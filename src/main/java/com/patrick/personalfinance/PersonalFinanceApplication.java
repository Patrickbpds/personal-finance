package com.patrick.personalfinance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PersonalFinanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonalFinanceApplication.class, args);
    }

}
