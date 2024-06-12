package com.desafiosupera.sistemadegerenciamentodetarefas.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardError> httpMessageNotReadable(HttpMessageNotReadableException httpMessageNotReadableException, HttpServletRequest request) {
        String error = "Resource not found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError standardError = new StandardError(Instant.now(), status.value(), error, httpMessageNotReadableException.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(standardError);
    }

    @ExceptionHandler(InvalidTaskStatusException.class)
    public ResponseEntity<StandardError> invalidTaskStatus(InvalidTaskStatusException resourceNotFoundException, HttpServletRequest request) {
        String error = "Task Status Invalid";
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError standardError = new StandardError(Instant.now(), status.value(), error, resourceNotFoundException.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(standardError);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(NotFoundException resourceNotFoundException, HttpServletRequest request) {
        String error = "Resource not found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError standardError = new StandardError(Instant.now(), status.value(), error, resourceNotFoundException.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(standardError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleValidationExceptions(MethodArgumentNotValidException methodArgumentNotValidException, HttpServletRequest request) {
        String errorTitle = "Invalid data";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<String> errors = new ArrayList();
        methodArgumentNotValidException.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errors.add("Error: " + errorMessage);
        });

        StandardError standardError = new StandardError(Instant.now(), status.value(), errorTitle, errors.get(0), request.getRequestURI());

        return ResponseEntity.status(status).body(standardError);
    }

    @ExceptionHandler(StandarTaskException.class)
    public ResponseEntity<StandardError> taskError(StandarTaskException taskException, HttpServletRequest request) {
        String error = "Task Error";
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError standardError = new StandardError(Instant.now(), status.value(), error, taskException.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(standardError);
    }
}
