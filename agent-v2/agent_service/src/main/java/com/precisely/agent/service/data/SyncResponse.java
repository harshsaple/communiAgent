package com.precisely.agent.service.data;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@JsonTypeName
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated
public class SyncResponse extends SyncRequest {

    @JsonProperty("responseBody")
    private String responseBody;

    @JsonProperty("endTime")
    private long endTime;

    @JsonProperty("responseStatus")
    private String responseStatus;

}
