package com.precisely.agent.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.precisely.agent.service.data.AsyncRequestResponse;
import com.precisely.agent.service.data.SyncRequest;
import com.precisely.agent.service.data.SyncResponse;
import com.precisely.agent.service.exception.TransportServiceException;
import com.precisely.agent.service.processor.request.RequestProcessor;
import com.precisely.agent.service.processor.response.ResponseCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/async")
public class AsyncCommunicationController {

    private static final Logger logger = LoggerFactory.getLogger(AsyncCommunicationController.class);
    @Autowired
    RequestProcessor requestProcessor;

    @Autowired
    ResponseCache responseCache;

    ObjectMapper mapper = new ObjectMapper();

    ExecutorService executor = Executors.newFixedThreadPool(10);

    private String buildErrorMessage(SyncRequest syncRequest, String requestID) {

        //TODO: Generate a Response and Return

        return null;
    }

    @PostMapping(value = "/request")
    public ResponseEntity<String> performSyncRequest(@RequestBody SyncRequest syncRequest) {

        String requestId = UUID.randomUUID().toString();

        SyncResponse syncResponse = null;
        String response;

        try {
            syncRequest.setRequestId(requestId);
            syncRequest.setStartTime(System.currentTimeMillis());
            //response = requestProcessor.performSyncRequest(syncRequest);
            //TODO:Send message using threadpool executor
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        requestProcessor.sendRequest(syncRequest);
                    } catch (TransportServiceException e) {
                        logger.info(String.valueOf(e));
                    }
                    //TODO: Try runtime exception
                }
            });
            AsyncRequestResponse asyncRequestResponse = new AsyncRequestResponse();
            asyncRequestResponse.setRequestId(requestId);
            return ResponseEntity.ok(mapper.writeValueAsString(asyncRequestResponse));


        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping(value = "/response/{requestId}")
    public ResponseEntity<String> performSyncRequest(@PathVariable String requestId) {

        SyncResponse syncResponse = null;


        try {

            syncResponse = requestProcessor.getResponseFor(requestId);
            syncResponse.setEndTime(System.currentTimeMillis());
            return ResponseEntity.ok(mapper.writeValueAsString(syncResponse));


        } catch (TransportServiceException e) {

            logger.error(" Exception caused by ", e);

            //buildErrorMessage(syncRequest,requestID)

        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
