package com.precisely.sqs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsS3Configuration {

    @Value("${s3.name}")
    private String s3Name;

    public String getS3Name() {
        return s3Name;
    }

    public void setS3Name(String s3Name) {
        this.s3Name = s3Name;
    }


}
