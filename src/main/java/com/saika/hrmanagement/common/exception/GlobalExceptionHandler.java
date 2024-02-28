/*
 * Copyright 2022 the original author or authors.
 * Licensed under the Saika Technologies Inc License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.saika.com/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.saika.hrmanagement.common.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mani
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static Map<String, Object> constructErrorResponse(Boolean isSuccess, String exMessage, HttpStatus httpStatus, List<String> error) {
        Map<String, Object> response = new HashMap<>();
        String message = null;
        response.put("status", isSuccess ? "success" : "failure");
        response.put("statusMessage", httpStatus);
        response.put("statusCode", httpStatus.value());
        response.put("errors", error);
        response.put("timestamp", LocalDateTime.now().toString());

        //if the default message is null,
        //let's construct a message based on the HTTP status
        switch (httpStatus) {
            case NOT_FOUND:
                message = "The resource does not exist";
                break;
            case INTERNAL_SERVER_ERROR:
                message = "Something went wrong internally";
                break;
            case FORBIDDEN:
                message = "Permission denied";
                break;
            case TOO_MANY_REQUESTS:
                message = "Too many requests";
                break;
            default:
                message = httpStatus.getReasonPhrase();
        }
        response.put("message", message);

        return response;
    }

    @ExceptionHandler(CustomApplicationException.class)
    public ResponseEntity<Map<String, Object>> handleCustomApplicationException(CustomApplicationException e) {
        // casting the generic Exception e to CustomErrorException
        CustomApplicationException customApplicationException = e;

        HttpStatus status = customApplicationException.getHttpStatus();
        String message = e.getMessage();
        Map<String, Object> response =  constructErrorResponse(false, e.getMessage(),   status, !Objects.isNull(e.getErrors()) ? e.getErrors() : null);
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest reques) {
        String error = ex.getMessage();
        Map<String, Object> response = constructErrorResponse(false, "It seems you're using the wrong HTTP method",  HttpStatus.METHOD_NOT_ALLOWED, Collections.singletonList(error));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = ex.getMessage();
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        Map<String, Object> response = constructErrorResponse(false, message,  HttpStatus.BAD_REQUEST, errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> constraintViolationException(ConstraintViolationException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();
        ex.getConstraintViolations().forEach(cv -> errors.add(cv.getMessage()));
        Map<String, Object> response = constructErrorResponse(false, ex.getMessage(),  HttpStatus.BAD_REQUEST, errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


}