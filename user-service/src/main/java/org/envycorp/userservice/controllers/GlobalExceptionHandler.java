package org.envycorp.userservice.controllers;

import org.envycorp.userservice.exceptions.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .collect(Collectors.groupingBy(
                                FieldError::getField,
                                Collectors.mapping(
                                        DefaultMessageSourceResolvable::getDefaultMessage,
                                        Collectors.toList()
                                ))
                        )
        );
    }

    @ExceptionHandler({
            EmailIsAlreadyTakenException.class,
            IncorrectEmailException.class,
            IncorrectPasswordException.class,
            TokenExpiredException.class,
            UserNotFoundException.class
    })
    public ResponseEntity<String> handleEmailIsAlreadyTaken(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
