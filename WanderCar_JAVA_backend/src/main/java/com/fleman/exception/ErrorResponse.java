package com.fleman.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private final String message;
    private final int status;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() { return message; }
    public int getStatus() { return status; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
