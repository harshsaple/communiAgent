package com.precisely.onprem.agent.processor;

import com.precisely.onprem.agent.producer.OnPremAgentResponseProducer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OnPremAgentRequestProcessor implements Runnable {

    @Autowired
    OnPremAgentResponseProducer onPremAgentResponseProducer;

    private String message;

    public OnPremAgentRequestProcessor(String message) {
        this.message = message;
    }

    @Override
    public void run() {
        onPremAgentResponseProducer.produceMesssage(appendMessage());
    }

    private String appendMessage() {

        JSONObject json = new JSONObject(message);
        String requestPayload = json.getString("requestPayload");
        String responseBody = "Hello " + requestPayload;
        json.remove(requestPayload);
        json.put("responseBody", responseBody);
        return json.toString();
    }

}
