package com.precisely.sqs.receiver;

import com.precisely.sqs.config.AwsSqsConfiguration;
import com.precisely.sqs.config.SqsClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MessageReceiver implements Runnable {
    @Autowired
    private AwsSqsConfiguration awsSqsConfiguration;

    @Autowired
    private SqsClientService sqsClientService;

    @Override
    public void run() {
        try {
            this.readMsg();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readMsg() {
        String sqsUrl = awsSqsConfiguration.getSqsUrl();
        //bean  'SqsClient' Singleton & Prototype bean
        SqsClient sqs = sqsClientService.getSqs();

        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
            .queueUrl(sqsUrl)
            .maxNumberOfMessages(10)
            .build();

        while (true) {
            List<Message> messages = sqs.receiveMessage(receiveMessageRequest).messages();
            for (Message m : messages) {
                System.out.println("Received message ID: " + m.messageId());
                System.out.println("Received message body: " + m.body());
                DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                    .queueUrl(sqsUrl)
                    .receiptHandle(m.receiptHandle())
                    .build();
                sqs.deleteMessage(deleteMessageRequest);
            }
        }
    }
}

