package com.precisely.agent.service.repository.agent;

import com.precisely.agent.service.data.AgentDetails;
import com.precisely.agent.service.data.SQSDetails;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AgentRepository {

    ConcurrentHashMap<String, AgentDetails> map = new ConcurrentHashMap<>();


    @PostConstruct
    private void setup() {

        SQSDetails sqsDetailsForAmex = new SQSDetails("AmericanExpress-User",
            "AKIAZV3GNOBZQXD56MW6", "jaYW/UGCSlX2tnJ9TobauUXBOVMy0hsO8lmy3PgA", "AP.South1", "AmericanExpress-Queue"
            , "https://sqs.ap-south-1.amazonaws.com/665397850227/AmericanExpress-Queue");
        map.put("AmericanExpress", new AgentDetails("AmericanExpress", "ConnectAgent", "", sqsDetailsForAmex));

        SQSDetails sqsDetailsForCiti = new SQSDetails("Citi-User", "AKIAZV3GNOBZ4LB36HHF", "/3x+uvtkOyH88iILGzegLpMIKOnRpwR6M+UuGMST",
            "AP.South1", "Citi-Queue", "https://sqs.ap-south-1.amazonaws.com/665397850227/Citi-Queue");
        map.put("Citi", new AgentDetails("Citi", "ConnectAgent", "", sqsDetailsForCiti));
    }

    public AgentDetails getAgentDetails(String agentId) {
        return map.get(agentId);
    }
}
