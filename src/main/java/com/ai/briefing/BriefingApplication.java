package com.ai.briefing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BriefingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BriefingApplication.class, args);
    }
}
