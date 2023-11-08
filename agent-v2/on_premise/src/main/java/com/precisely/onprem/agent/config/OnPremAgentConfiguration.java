package com.precisely.onprem.agent.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeName
@Generated
public class OnPremAgentConfiguration {

    //TODO :Configuration json files should be mapped here

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("accessKey")
    private String accessKey;

    @JsonProperty("secretKey")
    private String secretKey;

    @JsonProperty("region")
    private String region;

    @JsonProperty("onPremQueueName")
    private String onPremQueueName;

    @JsonProperty("onPremQueueUrl")
    private String onPremQueueUrl;

}
