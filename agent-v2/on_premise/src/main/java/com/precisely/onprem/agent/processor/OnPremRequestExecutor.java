package com.precisely.onprem.agent.processor;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class OnPremRequestExecutor implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private ThreadPoolExecutor executor = null;

    @PostConstruct
    public void init() {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    public void executeMessage(String message) {
        OnPremAgentRequestProcessor onPremAgentRequestProcessor = applicationContext.getBean(OnPremAgentRequestProcessor.class, message);
        executor.execute(onPremAgentRequestProcessor);

    }
}
