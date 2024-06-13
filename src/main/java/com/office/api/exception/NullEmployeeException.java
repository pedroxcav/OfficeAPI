package com.office.api.exception;

public class NullEmployeeException extends RuntimeException {
    public NullEmployeeException() {
        super("Employee does not exist");
    }
}
