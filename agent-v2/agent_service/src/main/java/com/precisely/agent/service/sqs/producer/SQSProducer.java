package com.precisely.agent.service.sqs.producer;

import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazon.sqs.javamessaging.ExtendedClientConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.precisely.agent.service.config.ServicConfiguration;
import com.precisely.agent.service.data.AgentDetails;
import com.precisely.agent.service.data.SQSDetails;
import com.precisely.agent.service.data.SyncRequest;
import com.precisely.agent.service.exception.TransportServiceException;
import com.precisely.agent.service.repository.agent.AgentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import javax.annotation.PostConstruct;

@Service
public class SQSProducer {
    private static final Logger logger = LoggerFactory.getLogger(SQSProducer.class);
    @Autowired
    AgentRepository repository;
    @Autowired
    ServicConfiguration servicConfiguration;
    ObjectMapper mapper = new ObjectMapper();
    private S3Client s3Client = null;
    private ExtendedClientConfiguration extendedClientConfiguration = null;
    private SqsClient sqsClient = null;
    private AmazonSQSExtendedClient sqsExtended = null;
    private SendMessageRequest extendedMessageRequest = null;
    private SendMessageResponse messageResponseResult = null;
    private SdkHttpResponse sdkHttpResponse = null;

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

    }

    public boolean postRequest(SyncRequest request) throws TransportServiceException {
        AgentDetails agentDetails = repository.getAgentDetails(request.getAgentId());

        SQSDetails sqsDetails = agentDetails.getSQSDetails();

        try {
            extendedMessageRequest = SendMessageRequest
                .builder()
                .queueUrl(sqsDetails.getPublishSqsQueueUrl())
                .messageBody(mapper.writeValueAsString(request))
                .build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new TransportServiceException("Request is not in valid format", 3);
        }

        messageResponseResult = sqsExtended.sendMessage(extendedMessageRequest);

        sdkHttpResponse = messageResponseResult.sdkHttpResponse();

        logger.info("Message ID: " + messageResponseResult.messageId());
        logger.info("Message Delivered Successfully: " + String.valueOf(sdkHttpResponse.isSuccessful()));
        logger.info("Response Code: " + String.valueOf(sdkHttpResponse.statusCode()));

        return true;

    }
}
