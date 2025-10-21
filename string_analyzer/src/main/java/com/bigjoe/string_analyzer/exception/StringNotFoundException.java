package com.bigjoe.string_analyzer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StringNotFoundException extends RuntimeException {
    public StringNotFoundException(String message) {
        super(message);
    }
}
