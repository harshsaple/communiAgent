package com.precisely.agent.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/executes")
public class RequestController {

    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);
    @Autowired
    RequestProcessor requestProcessor;

    @Autowired
    ResponseCache responseCache;

    ObjectMapper mapper = new ObjectMapper();

    private String buildErrorMessage(SyncRequest syncRequest, String requestID) {

        //TODO: Generate a Response and Return

        return null;
    }

    @PostMapping(value = "/sync/request")
    public ResponseEntity<String> performSyncRequest(@RequestBody SyncRequest syncRequest) {

        String requestID = UUID.randomUUID().toString();

        SyncResponse syncResponse = null;
        String response;


        try {
            syncRequest.setRequestId(requestID);
            syncRequest.setStartTime(System.currentTimeMillis());
            response = requestProcessor.performSyncRequest(syncRequest);
            syncResponse = mapper.readValue(response, SyncResponse.class);
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
