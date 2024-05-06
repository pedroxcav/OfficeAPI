package com.office.api.exception;

public class RegisteredCompanyException extends RuntimeException {
    public RegisteredCompanyException() {
        super("Company already registered");
    }
}
