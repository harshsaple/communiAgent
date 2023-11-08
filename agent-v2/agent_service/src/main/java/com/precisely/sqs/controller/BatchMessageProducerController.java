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
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchResponse;
import software.amazon.awssdk.services.sqs.model.SqsException;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;

@RestController
@RequestMapping("/sqs")
public class BatchMessageProducerController {

    private static final Logger logger = LoggerFactory.getLogger(BatchMessageProducerController.class);
    @Autowired
    AwsSqsConfiguration awsSqsConfiguration;
    @Autowired
    AwsS3Configuration awsS3Configuration;
    private String sqsURL = null;
    private String s3Name;
    private S3Client s3Client = null;
    private ExtendedClientConfiguration extendedClientConfiguration = null;
    private SqsClient sqsClient = null;
    private AmazonSQSExtendedClient sqsExtended = null;

    private SendMessageBatchRequest sendMessageBatchRequest = null;
    private SendMessageBatchRequestEntry sendMessageBatchRequestEntry = null; //result
    private SendMessageBatchResponse result = null;

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
            .build();

        sqsExtended = new AmazonSQSExtendedClient(sqsClient, extendedClientConfiguration);

    }

    @PostMapping(value = "/send_batch/{agentID}")
    public HttpResponseStatus sendBatchMsg(@RequestBody String message, @PathVariable(value = "agentID") String agentID) {

        sqsURL = agentID;

        try {

            String[] messages = message.split("\\n");

            Collection<SendMessageBatchRequestEntry> batchMessages = Arrays.asList(
                SendMessageBatchRequestEntry.builder().id("id-1").messageBody(messages[0]).build(),
                SendMessageBatchRequestEntry.builder().id("id-2").messageBody(messages[1]).build(),
                SendMessageBatchRequestEntry.builder().id("id-3").messageBody(messages[2]).build());

            sendMessageBatchRequest = SendMessageBatchRequest.builder()
                .queueUrl(sqsURL)
                .entries(batchMessages)
                .build();

            logger.info("Sending Batch Message from Producer");

            result = sqsExtended.sendMessageBatch(sendMessageBatchRequest);

            sdkHttpResponse = result.sdkHttpResponse();

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
