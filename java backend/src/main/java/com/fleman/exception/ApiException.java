package com.fleman.exception;

// General-purpose business-rule failure, carrying the HTTP status it
// should map to — mirrors the frontend mock's ApiError(message, status)
// class in api/client.js exactly, so error handling stays symmetric on
// both sides of the wire.
public class ApiException extends RuntimeException {
    private final int status;

    public ApiException(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() { return status; }
}
