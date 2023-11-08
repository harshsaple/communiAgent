package com.precisely.agent.service.processor.request;

import com.precisely.agent.service.data.SyncRequest;
import com.precisely.agent.service.data.SyncResponse;
import com.precisely.agent.service.exception.TransportServiceException;
import com.precisely.agent.service.processor.response.ResponseProcessor;
import com.precisely.agent.service.sqs.producer.SQSProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RequestProcessor.class);

    @Autowired
    SQSProducer producer;
    @Autowired
    ResponseProcessor responseProcessor;

    public String performSyncRequest(SyncRequest syncRequest) throws TransportServiceException {

        boolean result = producer.postRequest(syncRequest);

        if (result) {
            return responseProcessor.pollForResponse(syncRequest);
        } else {
            throw new TransportServiceException("Unable to post a message on queue", 1);
            //TODO: Create enum for error codes
        }

    }

    public void sendRequest(SyncRequest syncRequest) throws TransportServiceException {
        boolean result = producer.postRequest(syncRequest);

        if (result) {
            logger.info(" Successfully sent requestId" + syncRequest.getRequestId());
        } else {
            throw new TransportServiceException("Unable to send request for requestId" + syncRequest.getRequestId(), 4);
            //TODO: Create enum for error codes
        }
    }

    public SyncResponse getResponseFor(String requestId) throws TransportServiceException {

        return responseProcessor.getResponse(requestId);

    }
}

