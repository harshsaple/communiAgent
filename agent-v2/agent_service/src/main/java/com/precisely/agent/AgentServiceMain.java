package com.precisely.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.precisely.agent")
public class AgentServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(AgentServiceMain.class, args);
    }

}
