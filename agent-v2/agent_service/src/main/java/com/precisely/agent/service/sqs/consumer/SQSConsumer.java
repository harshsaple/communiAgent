package com.precisely.agent.service.sqs.consumer;

import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazon.sqs.javamessaging.ExtendedClientConfiguration;
import com.precisely.agent.service.config.ServicConfiguration;
import com.precisely.agent.service.processor.response.ResponseProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SQSConsumer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SQSConsumer.class);
    @Autowired
    ServicConfiguration servicConfiguration;
    @Autowired
    ResponseProcessor responseProcessor;
    private S3Client s3Client = null;
    private ExtendedClientConfiguration extendedClientConfiguration = null;
    private AmazonSQSExtendedClient sqsExtended = null;
    private SqsClient sqsClient = null;
    private ReceiveMessageRequest receiveMessageRequest = null;
    private ReceiveMessageResponse receiveMessageResponse = null;

    @Override
    public void run() {
        try {
            this.consumeAcknowledgmentMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void init() {

        s3Client = S3Client.builder()
            .region(Region.AP_SOUTH_1)
            .build();

        extendedClientConfiguration =
            new ExtendedClientConfiguration().withPayloadSupportEnabled(s3Client, servicConfiguration.getS3Name());

        sqsClient = SqsClient.builder()
            .region(Region.AP_SOUTH_1)
            .build();

        sqsExtended = new AmazonSQSExtendedClient(sqsClient, extendedClientConfiguration);

        receiveMessageRequest = ReceiveMessageRequest
            .builder()
            .queueUrl(servicConfiguration.getResponseQueueURL())
            .maxNumberOfMessages(10)
            .waitTimeSeconds(10)
            .build();
    }

    public void consumeAcknowledgmentMessage() {

        DeleteMessageRequest deleteMessageRequest;
        DeleteMessageResponse deleteMessageResponse;

        receiveMessageResponse = sqsExtended.receiveMessage(receiveMessageRequest);

        List<Message> messages = receiveMessageResponse.messages();

        for (Message m : messages) {
            logger.info("Message Received!");
            logger.info("  ID: " + m.messageId());
            logger.info("  Message body (first 10 characters): " + m.body());

            responseProcessor.storeResponse(m.body());

            deleteMessageRequest = DeleteMessageRequest.builder().queueUrl(servicConfiguration.getResponseQueueURL()).receiptHandle(m.receiptHandle()).build();
            deleteMessageResponse = sqsExtended.deleteMessage(deleteMessageRequest);
            logger.info(String.valueOf(deleteMessageResponse.sdkHttpResponse().isSuccessful()));
            logger.info(String.valueOf(deleteMessageResponse.sdkHttpResponse().statusCode()));

            System.out.println();
        }
    }
}
