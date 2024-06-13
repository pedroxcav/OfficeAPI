package com.office.api.exception;

public class NullTeamException extends RuntimeException {
    public NullTeamException() {
        super("Team does not exist");
    }
}
