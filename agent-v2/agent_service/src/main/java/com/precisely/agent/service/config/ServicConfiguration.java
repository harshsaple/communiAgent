package com.precisely.agent.service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
//@ConfigurationProperties(prefix = "precisely.agent.service.config")
public class ServicConfiguration {

    @Value("${precisely.agent.service.config.aws.sqs.masterUser.accessKey}")
    private String awsAccessKey;

    @Value("${precisely.agent.service.config.aws.sqs.masterUser.secretKey}")
    private String awsSecretKey;

    @Value("${precisely.agent.service.config.aws.sqs.masterUser.region}")
    private String awsRegion;

    @Value("${precisely.agent.service.config.aws.sqs.responseQueue.url}")
    private String responseQueueURL;

    @Value("${precisely.agent.service.config.aws.sqs.responseQueue.name}")
    private String responseQueueName;

    @Value("${precisely.agent.service.config.aws.s3.name}")
    private String s3Name;


}
