package com.precisely.sqs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsSqsConfiguration {
    @Value("${sqs.url}")
    private String sqsUrl;

    @Value("${sqs.name}")
    private String sqsName;

    @Value("${aws.accessKey}")
    private String awsAccessKey;

    @Value("${aws.secretKey}")
    private String awsSecretKey;

    @Value("${aws.region}")
    private String awsRegion;

    public AwsSqsConfiguration() {
        this.sqsUrl = "";
        this.awsAccessKey = "";
        this.awsRegion = "";
        this.awsSecretKey = "";
    }

    public String getSqsUrl() {
        return sqsUrl;
    }

    public void setSqsUrl(String sqsUrl) {
        this.sqsUrl = sqsUrl;
    }

    public String getSqsName() {
        return sqsName;
    }

    public void setSqsName(String sqsName) {
        this.sqsName = sqsName;
    }

    public String getAwsAccessKey() {
        return awsAccessKey;
    }

    public void setAwsAccessKey(String awsAccessKey) {
        this.awsAccessKey = awsAccessKey;
    }

    public String getAwsSecretKey() {
        return awsSecretKey;
    }

    public void setAwsSecretKey(String awsSecretKey) {
        this.awsSecretKey = awsSecretKey;
    }

    public String getAwsRegion() {
        return awsRegion;
    }

    public void setAwsRegion(String awsRegion) {
        this.awsRegion = awsRegion;
    }
}
