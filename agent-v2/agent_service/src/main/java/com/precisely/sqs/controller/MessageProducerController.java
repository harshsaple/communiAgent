package com.precisely.sqs.controller;

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
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import software.amazon.awssdk.services.sqs.model.SqsException;

import javax.annotation.PostConstruct;


@RestController
@RequestMapping("/sqs")
public class MessageProducerController {

    private static final Logger logger = LoggerFactory.getLogger(MessageProducerController.class);
    @Autowired
    private AwsSqsConfiguration awsSqsConfiguration;
    private String sqsURL;
    private String accessKey = null;
    private String secretAccessKey = null;
    private SqsClient sqsClient = null;

    private SendMessageResponse result = null;

    private SdkHttpResponse sdkHttpResponse = null;


    @PostConstruct
    public void init() {

        accessKey = awsSqsConfiguration.getAwsAccessKey();
        secretAccessKey = awsSqsConfiguration.getAwsSecretKey();

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

        //sqsURL = awsSqsConfiguration.getSqsURL();
    }

    @PostMapping(value = "/send/{agentID}")
    public HttpResponseStatus sendMsg(@RequestBody String message, @PathVariable(value = "agentID") String agentID) {


        try {

            if (agentID.equals("queue1")) {
                sqsURL = "https://sqs.ap-south-1.amazonaws.com/665397850227/queue1";
            } else if (agentID.equals("queue2")) {
                sqsURL = "https://sqs.ap-south-1.amazonaws.com/665397850227/queue2-secondInstance";
            } else {
                return null;
            }

            logger.info("Sending Message from Producer");

            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(sqsURL)
                .messageBody(message)
                .build();

            result = sqsClient.sendMessage(sendMessageRequest);

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


