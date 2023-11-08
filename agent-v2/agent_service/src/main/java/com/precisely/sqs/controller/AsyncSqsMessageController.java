package com.precisely.sqs.controller;

import com.precisely.sqs.config.AwsS3Configuration;
import com.precisely.sqs.config.AwsSqsConfiguration;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.*;

import javax.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/sqs")
public class AsyncSqsMessageController {

    private static final Logger logger = LoggerFactory.getLogger(AsyncSqsMessageController.class);
    @Autowired
    AwsSqsConfiguration awsSqsConfiguration;
    @Autowired
    AwsS3Configuration awsS3Configuration;
    private String queueName = null;
    private String accessKey = null;
    private String secretAccessKey = null;
    private String s3Name;
    private SqsAsyncClient sqsAsyncClient = null;
    private CompletableFuture<GetQueueUrlResponse> queueResponse = null;
    private GetQueueUrlResponse getQueueUrlResponse = null;
    private Mono<SendMessageResponse> sendMessageResponseMono = null;

    @PostConstruct
    public void init() {

        accessKey = awsSqsConfiguration.getAwsAccessKey();
        secretAccessKey = awsSqsConfiguration.getAwsSecretKey();

        sqsAsyncClient = SqsAsyncClient.builder()
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

    }

    @PostMapping(value = "/send_async/{agentName}")
    public HttpResponseStatus sendAsyncMessage(@RequestBody String message, @PathVariable(value = "agentName") String agentName) throws Exception {

        try {
            if (agentName.equals("queue1")) {
                queueName = "queue1";
            } else if (agentName.equals("queue2")) {
                queueName = "queue2-secondInstance";
            } else if (agentName.equals("clientGroup1queue")) {
                queueName = "clientGroup1queue";
            } else {
                return null;
            }

            queueResponse = sqsAsyncClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build());

            logger.info("Sending Async Message");

            getQueueUrlResponse = queueResponse.get();

            sendMessageResponseMono = Mono.fromFuture(() -> sqsAsyncClient.sendMessage(
                SendMessageRequest.builder()
                    .queueUrl(getQueueUrlResponse.queueUrl())
                    .messageBody(message + ZonedDateTime.now())
                    .build()
            ));

            sendMessageResponseMono.retryWhen(Retry.max(3)).subscribe(sendMessageResponse -> {
                    logger.info("Message ID: " + sendMessageResponse.messageId());
                    logger.info("Response Code: " + String.valueOf(sendMessageResponse.sdkHttpResponse().statusCode()));
                }
            );
        } catch (SqsException e) {

            logger.info(e.getMessage());
            logger.error(e.awsErrorDetails().errorMessage());
            return HttpResponseStatus.INTERNAL_SERVER_ERROR;

        }
        return null;
    }

}
