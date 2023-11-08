package com.precisely.sqs.controller;

import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazon.sqs.javamessaging.ExtendedClientConfiguration;
import com.precisely.sqs.config.AwsS3Configuration;
import com.precisely.sqs.config.AwsSqsConfiguration;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import software.amazon.awssdk.services.sqs.model.SqsException;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/sqs")
public class LargeMessageController {

    private static final Logger logger = LoggerFactory.getLogger(LargeMessageController.class);
    @Autowired
    private AwsSqsConfiguration awsSqsConfiguration;
    private String sqsURL = null;
    @Autowired
    private AwsS3Configuration awsS3Configuration;
    private String s3Name;
    private String accessKey = null;
    private String secretAccessKey = null;
    private S3Client s3Client = null;
    private ExtendedClientConfiguration extendedClientConfiguration = null;
    private SqsClient sqsClient = null;
    private AmazonSQSExtendedClient sqsExtended = null;
    private SendMessageRequest extendedMessageRequest = null;
    private SendMessageResponse result = null;
    private SdkHttpResponse sdkHttpResponse = null;

    @PostConstruct
    public void init() {
        s3Name = awsS3Configuration.getS3Name();

        s3Client = S3Client.builder()
            .region(Region.AP_SOUTH_1)
            .build();

        extendedClientConfiguration =
            new ExtendedClientConfiguration().withPayloadSupportEnabled(s3Client, s3Name);

        sqsClient = SqsClient.builder()
            .region(Region.AP_SOUTH_1)
            .credentialsProvider(StaticCredentialsProvider.create(new AwsCredentials() {
                @Override
                public String accessKeyId() {
                    return accessKey;
                }

                @Override
                public String secretAccessKey() {
                    return secretAccessKey;
                }
            }))
            .build();

        sqsExtended = new AmazonSQSExtendedClient(sqsClient, extendedClientConfiguration);
    }

    @PostMapping(value = "/send_extended/{agentID}")
    public HttpResponseStatus sendExtendedMessage(@RequestBody String message, @PathVariable(value = "agentID") String agentID) {

        try {

            if (agentID.equals("queue1")) {
                sqsURL = "https://sqs.ap-south-1.amazonaws.com/665397850227/queue1";
            } else if (agentID.equals("queue2")) {
                sqsURL = "https://sqs.ap-south-1.amazonaws.com/665397850227/queue2-secondInstance";
            } else {
                return null;
            }

            extendedMessageRequest = SendMessageRequest.builder()
                .queueUrl(sqsURL)
                .messageBody(message)
                .build();

            result = sqsExtended.sendMessage(extendedMessageRequest);

            sdkHttpResponse = result.sdkHttpResponse();

            logger.info("Message ID: " + result.messageId());
            logger.info("Message Delivered Successfully: " + String.valueOf(sdkHttpResponse.isSuccessful()));
            logger.info("Response Code: " + String.valueOf(sdkHttpResponse.statusCode()));

        } catch (SqsException e) {
            logger.info(e.getMessage());
            logger.error(e.awsErrorDetails().errorMessage());
            return HttpResponseStatus.INTERNAL_SERVER_ERROR;

        }

        return null;
    }

}


