package com.office.api.exception;

public class InvalidDeadlineException extends RuntimeException {
    public InvalidDeadlineException() {
        super("Invalid deadline");
    }
}
