package com.spendguard;

import com.spendguard.config.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(JwtConfig.class)
public class SpendGuardApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpendGuardApplication.class, args);
    }
}