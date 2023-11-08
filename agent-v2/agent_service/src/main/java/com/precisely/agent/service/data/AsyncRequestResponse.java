package com.precisely.agent.service.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Generated
@JsonTypeName
public class AsyncRequestResponse {

    @JsonProperty
    private String requestId;
}
