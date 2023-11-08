package com.precisely.onprem.agent.consumer;

import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazon.sqs.javamessaging.ExtendedClientConfiguration;
import com.precisely.onprem.agent.config.OnPremAgentConfiguration;
import com.precisely.onprem.agent.config.OnPremConfiguration;
import com.precisely.onprem.agent.processor.OnPremRequestExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ExtendedMessageReceiver implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ExtendedMessageReceiver.class);
    OnPremConfiguration onPremConfiguration;
    OnPremRequestExecutor onPremRequestExecutor;
    OnPremAgentConfiguration onPremAgentConfiguration;
    private S3Client s3Client = null;
    private ExtendedClientConfiguration extendedClientConfig = null;
    private SqsClient sqsClient;
    private AmazonSQSExtendedClient sqsExtended = null;
    private ReceiveMessageRequest receiveMessageRequest = null;


    public ExtendedMessageReceiver(OnPremAgentConfiguration onPremAgentConfiguration, OnPremConfiguration onPremConfiguration, OnPremRequestExecutor onPremRequestExecutor) {
        this.onPremAgentConfiguration = onPremAgentConfiguration;
        this.onPremConfiguration = onPremConfiguration;
        this.onPremRequestExecutor = onPremRequestExecutor;
        this.init();
    }

    @Override
    public void run() {
        try {
            this.readLargeMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@PostConstruct
    public void init() {

        s3Client = S3Client.builder().region(Region.AP_SOUTH_1).build();

        extendedClientConfig = new ExtendedClientConfiguration().withPayloadSupportEnabled(s3Client, onPremConfiguration.getS3Name());

        sqsClient = SqsClient.builder().region(Region.AP_SOUTH_1).
            credentialsProvider(StaticCredentialsProvider.create(new AwsCredentials() {
                @Override
                public String accessKeyId() {
                    return onPremAgentConfiguration.getAccessKey();
                }

                @Override
                public String secretAccessKey() {
                    return onPremAgentConfiguration.getSecretKey();
                }
            })).build();

        sqsExtended = new AmazonSQSExtendedClient(sqsClient, extendedClientConfig);

        receiveMessageRequest = ReceiveMessageRequest.builder().queueUrl(onPremAgentConfiguration.getOnPremQueueUrl()).maxNumberOfMessages(10).
            waitTimeSeconds(10).build();
    }


    public void readLargeMessage() {
        ReceiveMessageResponse receiveMessageResponse;
        DeleteMessageRequest deleteMessageRequest;
        DeleteMessageResponse deleteMessageResponse;

        receiveMessageResponse = sqsExtended.receiveMessage(receiveMessageRequest);
        List<Message> messages = receiveMessageResponse.messages();
        for (Message m : messages) {
            logger.info("Message Received!");
            logger.info("  ID: " + m.messageId());
            logger.info("  Message body: "
                + m.body());

            String val = m.body();

            onPremRequestExecutor.executeMessage(val);

            deleteMessageRequest = DeleteMessageRequest.builder().queueUrl(onPremAgentConfiguration.getOnPremQueueUrl()).receiptHandle(m.receiptHandle()).build();
            deleteMessageResponse = sqsExtended.deleteMessage(deleteMessageRequest);
            logger.info(String.valueOf(deleteMessageResponse.sdkHttpResponse().isSuccessful()));
            logger.info(String.valueOf(deleteMessageResponse.sdkHttpResponse().statusCode()));

            System.out.println();
        }
    }
}

