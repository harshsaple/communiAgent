package com.precisely.onprem.agent.consumer;

import com.precisely.onprem.agent.config.OnPremAgentConfiguration;
import com.precisely.onprem.agent.config.OnPremConfiguration;
import com.precisely.onprem.agent.processor.OnPremRequestExecutor;
import com.precisely.onprem.agent.repo.OnPremClientRepo;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class OnPremRequestConsumer implements ApplicationContextAware {

    @Autowired
    OnPremClientRepo onPremClientRepo;
    @Autowired
    OnPremConfiguration onPremConfiguration;
    @Autowired
    OnPremRequestExecutor onPremRequestExecutor;
    private ApplicationContext applicationContext;
    private ScheduledThreadPoolExecutor executor;
    private OnPremAgentConfiguration agentConfig;

    @PostConstruct
    public void init() {
        agentConfig = onPremClientRepo.getAgentConfiguration();
        executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
        ExtendedMessageReceiver extendedMessageReceiver = applicationContext.getBean(ExtendedMessageReceiver.class, agentConfig, onPremConfiguration, onPremRequestExecutor);
        executor.scheduleAtFixedRate(extendedMessageReceiver, 2, 8, TimeUnit.SECONDS);

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}

