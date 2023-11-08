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
public class SQSDetails {


    @JsonProperty("userName")
    private String userName;

    @JsonProperty("accessKey")
    private String accessKey;

    @JsonProperty("secretKey")
    private String secretKey;

    @JsonProperty("region")
    private String region;

    @JsonProperty("publishSqsQueueName")
    private String publishSqsQueueName;

    @JsonProperty("publishSqsQueueUrl")
    private String publishSqsQueueUrl;

}
