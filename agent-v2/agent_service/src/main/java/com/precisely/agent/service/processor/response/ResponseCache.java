package com.precisely.agent.service.processor.response;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ResponseCache {
    private ConcurrentHashMap<String, ResponseTTLEntry> map = new ConcurrentHashMap<>();

    //TODO : Write a timer task which deletes the invalidate entries from cache


    public void putResponse(String requestId, String response) {
        if (!map.containsKey(requestId))
            map.put(requestId, new ResponseTTLEntry(response));
        else {
            ResponseTTLEntry responseTTLEntry = map.get(requestId);
            responseTTLEntry.setResponse(response);
        }
    }


    public String getResponse(String requestId) {

        ResponseTTLEntry responseTTLEntry = map.get(requestId);

        if (responseTTLEntry != null) {
            return responseTTLEntry.getResponse();

        }

        return null;
    }

    public boolean isKeyPresent(String requestId) {
        return map.containsKey(requestId);
    }

    public boolean isRequestTimeout(String requestId) {
        ResponseTTLEntry responseTTLEntry = map.get(requestId);

        if (responseTTLEntry != null) {
            return responseTTLEntry.isRequestTimeout();
        }
        return false; //by default false
    }
}
