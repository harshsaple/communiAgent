package com.precisely.agent.service.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@JsonTypeName
@Generated
@Getter
@Setter
@AllArgsConstructor
public class AgentDetails {

    @JsonProperty("agentId")
    private String agentId;

    @JsonProperty("agentName")
    private String agentName;

    @JsonProperty("agentDetails")
    private String agentDetails;

    @JsonProperty("sqsDetails")
    private SQSDetails sQSDetails;

}
