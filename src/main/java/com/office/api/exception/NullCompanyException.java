package com.office.api.exception;

public class NullCompanyException extends RuntimeException {
    public NullCompanyException() {
        super("Company does not exist");
    }
}
