package com.precisely.agent.service.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransportServiceException extends Exception {

    private int errorCode;

    public TransportServiceException(int errorCode) {
        this.errorCode = errorCode;
    }

    public TransportServiceException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public TransportServiceException(Throwable cause, int errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public TransportServiceException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
