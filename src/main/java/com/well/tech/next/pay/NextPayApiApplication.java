package com.well.tech.next.pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NextPayApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NextPayApiApplication.class, args);
    }

}
