package com.telerikacademy.socialalien.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Conflicting resource state.")
public class InvalidDbStateException extends RuntimeException {
    public InvalidDbStateException(String message) {
        super(message);
    }
}
