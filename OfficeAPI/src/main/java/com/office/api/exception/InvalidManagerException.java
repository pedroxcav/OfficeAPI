package com.office.api.exception;

public class InvalidManagerException extends RuntimeException {
    public InvalidManagerException() {
        super("Already manage a project");
    }
}
