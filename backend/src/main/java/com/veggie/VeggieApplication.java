package com.veggie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VeggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(VeggieApplication.class, args);
    }
}
