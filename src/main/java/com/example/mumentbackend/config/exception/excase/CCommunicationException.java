package com.example.mumentbackend.config.exception.excase;

public class CCommunicationException extends RuntimeException {
    public CCommunicationException() {
        super();
    }

    public CCommunicationException(String message) {
        super(message);
    }

    public CCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}