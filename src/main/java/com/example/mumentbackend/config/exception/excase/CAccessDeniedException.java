package com.example.mumentbackend.config.exception.excase;

public class CAccessDeniedException extends RuntimeException {
    public CAccessDeniedException() {
    }

    public CAccessDeniedException(String message) {
        super(message);
    }

    public CAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
