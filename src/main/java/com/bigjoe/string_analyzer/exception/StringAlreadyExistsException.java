package com.bigjoe.string_analyzer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class StringAlreadyExistsException extends RuntimeException {
    public StringAlreadyExistsException(String message) {
        super(message);
    }
}
