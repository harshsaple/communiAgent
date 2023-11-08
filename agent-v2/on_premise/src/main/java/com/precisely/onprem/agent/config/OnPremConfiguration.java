package com.precisely.onprem.agent.config;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@Generated
public class OnPremConfiguration {

    @Value("${precisely.agent.service.config.aws.sqs.responseQueue.url}")
    private String responseQueueURL;

    @Value("${precisely.agent.service.config.aws.sqs.responseQueue.name}")
    private String responseQueueName;

    @Value("${precisely.agent.service.config.aws.s3.name}")
    private String s3Name;

}
