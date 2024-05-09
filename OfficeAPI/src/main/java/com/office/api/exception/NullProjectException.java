package com.office.api.exception;

public class NullProjectException extends RuntimeException {
    public NullProjectException() {
        super("Project does not exist");
    }
}
