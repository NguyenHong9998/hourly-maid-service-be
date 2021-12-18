package com.example.hourlymaids;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.example")
public class HourlymaidsApplication {
    public static void main(String[] args) {
        SpringApplication.run(HourlymaidsApplication.class, args);
    }

}
