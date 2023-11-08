package com.precisely.agent.service.processor.response;

import com.precisely.agent.service.sqs.consumer.SQSConsumer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class ResponseQueueProcessor implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private ScheduledThreadPoolExecutor executor;

    @PostConstruct
    public void init() {
        executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(3);
        SQSConsumer sqsConsumer = applicationContext.getBean(SQSConsumer.class);
        executor.scheduleAtFixedRate(sqsConsumer, 10000, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
