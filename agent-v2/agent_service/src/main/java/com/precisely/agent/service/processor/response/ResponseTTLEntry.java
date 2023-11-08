package com.precisely.agent.service.processor.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseTTLEntry {
    String response;
    long ttl;
    long timeout;

    public ResponseTTLEntry(String response) {
        ttl = System.currentTimeMillis() + 10 * 60 * 1000;
        timeout = System.currentTimeMillis() + 5 * 60 * 1000;
        this.response = response;
    }

    public boolean isRequestTimeout() {
        return (System.currentTimeMillis() > timeout);
    }

    public boolean isTtlExpired() {
        return (System.currentTimeMillis() > ttl);
    }


}
