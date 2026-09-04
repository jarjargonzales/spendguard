package com.spendguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpendGuardApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpendGuardApplication.class, args);
    }
}
