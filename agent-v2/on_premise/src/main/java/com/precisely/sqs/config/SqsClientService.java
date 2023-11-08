package com.precisely.sqs.config;

import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class SqsClientService {
    SqsClient sqs = SqsClient.builder().region(Region.AP_SOUTH_1).build();

    public SqsClient getSqs() {
        return this.sqs;
    }


    public void setSqs(SqsClient sqs) {
        this.sqs = sqs;
    }
}
