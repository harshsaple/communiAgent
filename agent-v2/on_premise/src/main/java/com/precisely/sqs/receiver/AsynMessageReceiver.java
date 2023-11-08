package com.precisely.sqs.receiver;

import com.precisely.sqs.config.AwsSqsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import javax.annotation.PostConstruct;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AsynMessageReceiver implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsynMessageReceiver.class);
    @Autowired
    private AwsSqsConfiguration awsSqsConfiguration;
    private String sqsUrl = null;
    private String sqsName = null;
    private String accessKey = null;
    private String secretKey = null;
    private SqsAsyncClient sqsAsyncClient = null;
    private Mono<ReceiveMessageResponse> receiveMessageResponseMono = null;

    @PostConstruct
    public void init() {
        sqsUrl = awsSqsConfiguration.getSqsUrl();
        sqsName = awsSqsConfiguration.getSqsName();
        accessKey = awsSqsConfiguration.getAwsAccessKey();
        secretKey = awsSqsConfiguration.getAwsSecretKey();

        sqsAsyncClient = SqsAsyncClient.builder().region(Region.AP_SOUTH_1).
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
    }

    public void run() {
        try {
            this.asynReadMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void asynReadMessage() {
        receiveMessageResponseMono = Mono.fromFuture(() ->
            sqsAsyncClient.receiveMessage(
                ReceiveMessageRequest.builder()
                    .maxNumberOfMessages(5)
                    .queueUrl(sqsUrl)
                    .waitTimeSeconds(10)
                    .visibilityTimeout(30)
                    .build()
            )
        );

        receiveMessageResponseMono
            .repeat()
            .retry()
            .map(ReceiveMessageResponse::messages)
            .map(Flux::fromIterable)
            .flatMap(messageFlux -> messageFlux)
            .subscribe(message -> {
                LOGGER.info("Message body: " + message.body());
                LOGGER.info("Message ID: " + message.messageId());

                sqsAsyncClient.deleteMessage(DeleteMessageRequest.builder().queueUrl(sqsUrl).receiptHandle(message.receiptHandle()).build())
                    .thenAccept(deleteMessageResponse -> {
                        LOGGER.info("deleted message with handle " + message.receiptHandle());
                        System.out.println();
                    });
            });

    }
}
