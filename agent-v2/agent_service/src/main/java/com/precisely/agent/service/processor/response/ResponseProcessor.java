package com.precisely.agent.service.processor.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.precisely.agent.service.data.ResponseStatusEnum;
import com.precisely.agent.service.data.SyncRequest;
import com.precisely.agent.service.data.SyncResponse;
import com.precisely.agent.service.exception.TransportServiceException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class ResponseProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ResponseProcessor.class);
    private static int SLEEP_INTERVAL_MILLIS = 100;

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    ResponseCache responseCache;
    Duration waitTime = Duration.ofSeconds(60);

    private void sleepThreadForDuration(long duration) throws InterruptedException {
        Object LOCK = new Object();
        if (duration > 0) {
            boolean flag = true;
            while (flag) {
                synchronized (LOCK) {
                    LOCK.wait(duration);
                    flag = false;
                }
            }
        }
    }

    public String pollForResponse(SyncRequest syncRequest) throws TransportServiceException {

        int waitTimeMillis = (int) (waitTime.getSeconds() * 1000 + waitTime.getNano() / 1000000);
        long startTime = System.currentTimeMillis();
        long endTime = startTime + waitTimeMillis;
        // Wait for response file to arrive
        try {
            while (endTime > System.currentTimeMillis()) {
                String response = responseCache.getResponse(syncRequest.getRequestId());

                if (response != null) {
                    return response;
                }

                sleepThreadForDuration(SLEEP_INTERVAL_MILLIS);
            }

            throw new TransportServiceException("Response Timeout", 0);

        } catch (InterruptedException e) {
            logger.trace(e.toString());
            Thread.currentThread().interrupt();
            throw new TransportServiceException("Thread InterruptedException", 2);

        }
    }

    public void storeResponse(String response) {


        JSONObject json = new JSONObject(response);
        String requestID = json.getString("requestId");

        responseCache.putResponse(requestID, response);

    }

    public SyncResponse getResponse(String requestId) throws TransportServiceException {

        String response = responseCache.getResponse(requestId);

        SyncResponse syncResponse = null;
        syncResponse = new SyncResponse();
        syncResponse.setRequestId(requestId);


        if (response != null) {

            try {
                syncResponse = mapper.readValue(response, SyncResponse.class);
                syncResponse.setResponseStatus(ResponseStatusEnum.OK.toString());

                return syncResponse;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                syncResponse.setResponseStatus(ResponseStatusEnum.ERROR.toString());
            }

        } else if (responseCache.isKeyPresent(requestId)) {
            syncResponse.setResponseStatus(ResponseStatusEnum.WAITING_FOR_RESPONSE.toString());
        } else if (responseCache.isRequestTimeout(requestId)) {
            syncResponse.setResponseStatus(ResponseStatusEnum.RESPONSE_TIMEOUT.toString());

        }


        return syncResponse;
    }
}

