package com.office.api.exception.handler;

import org.springframework.http.HttpStatus;

public record ExceptionDTO(Integer statusCode,
                           HttpStatus status,
                           String title,
                           String message,
                           String timestamp) {
}
