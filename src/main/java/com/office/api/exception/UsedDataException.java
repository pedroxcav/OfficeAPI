package com.office.api.exception;

public class UsedDataException extends RuntimeException {
    public UsedDataException() {
        super("Data already in use");
    }
}
