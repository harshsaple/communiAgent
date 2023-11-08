package com.precisely.sqs.receiver;

import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazon.sqs.javamessaging.ExtendedClientConfiguration;
import com.precisely.sqs.config.AwsS3Configuration;
import com.precisely.sqs.config.AwsSqsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ExtendedMessageReceiver implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ExtendedMessageReceiver.class);
    @Autowired
    private AwsSqsConfiguration awsSqsConfiguration;
    @Autowired
    private AwsS3Configuration awsS3Configuration;
    private S3Client s3Client = null;
    private ExtendedClientConfiguration extendedClientConfig = null;
    private AmazonSQSExtendedClient sqsExtended = null;
    private String s3Name = null;
    private String sqsUrl = null;
    private SqsClient sqsClient = null;
    private String accessKey = null;
    private String secretKey = null;
    private ReceiveMessageRequest receiveMessageRequest = null;
    private ReceiveMessageResponse receiveMessageResponse = null;
    private List<Message> messages = null;
    private DeleteMessageRequest deleteMessageRequest = null;
    private DeleteMessageResponse deleteMessageResponse = null;

    @PostConstruct
    public void init() {

        accessKey = awsSqsConfiguration.getAwsAccessKey();

        secretKey = awsSqsConfiguration.getAwsSecretKey();

        s3Client = S3Client.builder().region(Region.AP_SOUTH_1).build();

        s3Name = awsS3Configuration.getS3Name();

        extendedClientConfig = new ExtendedClientConfiguration().withPayloadSupportEnabled(s3Client, s3Name);

        sqsUrl = awsSqsConfiguration.getSqsUrl();

        sqsClient = SqsClient.builder().region(Region.AP_SOUTH_1).
            credentialsProvider(StaticCredentialsProvider.create(new AwsCredentials() {
                @Override
                public String accessKeyId() {
                    return accessKey;
                }

                @Override
                public String secretAccessKey() {
                    return secretKey;
                }
            })).build();

        sqsExtended = new AmazonSQSExtendedClient(sqsClient, extendedClientConfig);

        receiveMessageRequest = ReceiveMessageRequest.builder().queueUrl(sqsUrl).maxNumberOfMessages(10).
            waitTimeSeconds(10).build();
    }

    @Override
    public void run() {
        try {
            this.readLargeMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readLargeMessage() {
        receiveMessageResponse = sqsExtended.receiveMessage(receiveMessageRequest);
        messages = receiveMessageResponse.messages();
        for (Message m : messages) {
            logger.info("Message Received!");
            logger.info("  ID: " + m.messageId());
            logger.info("  Receipt handle: " + m.receiptHandle());
            logger.info("  Message body (first 10 characters): "
                + m.body());
            deleteMessageRequest = DeleteMessageRequest.builder().queueUrl(sqsUrl).receiptHandle(m.receiptHandle()).build();
            deleteMessageResponse = sqsExtended.deleteMessage(deleteMessageRequest);
            logger.info(String.valueOf(deleteMessageResponse.sdkHttpResponse().isSuccessful()));
            logger.info(String.valueOf(deleteMessageResponse.sdkHttpResponse().statusCode()));

            System.out.println();
        }
    }
}

