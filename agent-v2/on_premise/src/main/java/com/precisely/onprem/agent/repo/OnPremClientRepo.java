package com.precisely.onprem.agent.repo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.precisely.onprem.agent.config.OnPremAgentConfiguration;
import org.springframework.stereotype.Repository;
import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Repository
public class OnPremClientRepo {

    private OnPremAgentConfiguration americanExpress;
    private OnPremAgentConfiguration citi;

    @PostConstruct
    public void init() {

        //TODO: Get agent id from repo and load the file and return

        ObjectMapper mapper = new ObjectMapper();

        File AmericanExpressCredentials = new File("on_premise/src/main/resources/AmericanExpressCredentials.json");
        File CitiCredentials = new File("on_premise/src/main/resources/CitiCredentials.json");

        try {
            americanExpress = mapper.readValue(AmericanExpressCredentials, OnPremAgentConfiguration.class);
            citi = mapper.readValue(CitiCredentials, OnPremAgentConfiguration.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(getAgentConfiguration());
    }

    public OnPremAgentConfiguration getAgentConfiguration() {

        String agentId = System.getenv().get("agentId");

        if (agentId.equals("AmericanExpress")) {
            return americanExpress;
        } else {
            return citi;
        }
    }
}
