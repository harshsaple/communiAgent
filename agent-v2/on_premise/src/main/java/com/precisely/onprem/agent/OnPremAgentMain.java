package com.precisely.onprem.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.precisely.onprem")
public class OnPremAgentMain {

    public static void main(String[] args) {
        SpringApplication.run(OnPremAgentMain.class, args);
    }

}
