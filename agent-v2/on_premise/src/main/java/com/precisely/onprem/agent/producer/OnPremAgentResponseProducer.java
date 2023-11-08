package com.precisely.onprem.agent.producer;

import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazon.sqs.javamessaging.ExtendedClientConfiguration;
import com.precisely.onprem.agent.config.OnPremAgentConfiguration;
import com.precisely.onprem.agent.config.OnPremConfiguration;
import com.precisely.onprem.agent.repo.OnPremClientRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import javax.annotation.PostConstruct;

@Service
public class OnPremAgentResponseProducer {

    private static final Logger logger = LoggerFactory.getLogger(OnPremAgentResponseProducer.class);
    @Autowired
    OnPremClientRepo onPremClientRepo;
    @Autowired
    OnPremConfiguration onPremConfiguration;
    private OnPremAgentConfiguration agentConfig;
    private S3Client s3Client = null;
    private SqsClient sqsClient = null;
    private ExtendedClientConfiguration extendedClientConfiguration = null;
    private AmazonSQSExtendedClient sqsExtended = null;

    @PostConstruct
    public void init() {
        agentConfig = onPremClientRepo.getAgentConfiguration();

        s3Client = S3Client.builder()
            .region(Region.AP_SOUTH_1)
            .build();

        extendedClientConfiguration =
            new ExtendedClientConfiguration().withPayloadSupportEnabled(s3Client, onPremConfiguration.getS3Name());

        sqsClient = SqsClient.builder()
            .region(Region.AP_SOUTH_1)
            //TODO:Get region dynamically
            .credentialsProvider(StaticCredentialsProvider.create(new AwsCredentials() {
                @Override
                public String accessKeyId() {
                    return agentConfig.getAccessKey();
                }

                @Override
                public String secretAccessKey() {
                    return agentConfig.getSecretKey();
                }
            }))
            .build();

        sqsExtended = new AmazonSQSExtendedClient(sqsClient, extendedClientConfiguration);

    }

    public void produceMesssage(String message) {

        SendMessageRequest extendedMessageRequest;
        SendMessageResponse messageResponseResult;

        extendedMessageRequest = SendMessageRequest.builder()
            .queueUrl(onPremConfiguration.getResponseQueueURL())
            .messageBody(message)
            .build();

        logger.info("Sending Response Message");
        messageResponseResult = sqsExtended.sendMessage(extendedMessageRequest);
        logger.info("Message ID: " + messageResponseResult.messageId());


    }

}
