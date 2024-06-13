package com.office.api.exception.handler;


import com.office.api.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<ExceptionDTO> handleLoginFailedException(RuntimeException exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN, "Access Forbidden", exception.getMessage(),
                formatter.format(LocalDateTime.now()));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionDTO);
    }
    @ExceptionHandler(UsedDataException.class)
    public ResponseEntity<ExceptionDTO> handleUsedDataException(RuntimeException exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST, "Provided Information", exception.getMessage(),
                formatter.format(LocalDateTime.now()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDTO);
    }
    @ExceptionHandler({InvalidEmployeeException.class, InvalidDeadlineException.class})
    public ResponseEntity<ExceptionDTO> handleInvalidException(RuntimeException exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST, "Invalid Data", exception.getMessage(),
                formatter.format(LocalDateTime.now()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDTO);
    }
    @ExceptionHandler({
            NullTaskException.class,
            NullTeamException.class,
            NullProjectException.class,
            NullCommentException.class,
            NullCompanyException.class,
            NullEmployeeException.class})
    public ResponseEntity<ExceptionDTO> handleNullException(RuntimeException exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND, "Non-existent Information", exception.getMessage(),
                formatter.format(LocalDateTime.now()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDTO);
    }
}
