package com.precisely.agent.service.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Data
@JsonTypeName
@Generated
@Getter
@Setter
public class SyncRequest {

    @JsonProperty("requestId")
    private String requestId;

    @JsonProperty("agentId")
    private String agentId;

    @JsonProperty("productName")
    private String productName;

    @JsonProperty("requestPayload")
    private String requestPayload;

    @JsonProperty("requestIdentifier")
    private String requestIdentifier;

    @JsonProperty("serviceRequestId")
    private String serviceRequestId;

    @JsonProperty("startTime")
    private long startTime;


}
