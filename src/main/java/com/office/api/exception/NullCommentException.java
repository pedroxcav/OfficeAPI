package com.office.api.exception;

public class NullCommentException extends RuntimeException {
    public NullCommentException() {
        super("Comment does not exist");
    }
}
