package com.office.api.exception;

public class NullTaskException extends RuntimeException {
    public NullTaskException() {
        super("Task does not exist");
    }
}
