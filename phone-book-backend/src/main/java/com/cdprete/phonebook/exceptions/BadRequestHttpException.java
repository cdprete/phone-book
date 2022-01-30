package com.cdprete.phonebook.exceptions;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @author Cosimo Damiano Prete
 * @since 08/02/2022
 */
public abstract class BadRequestHttpException extends ClientHttpException {
    public BadRequestHttpException(String message) {
        super(message);
    }

    public final HttpStatus statusCode() {
        return BAD_REQUEST;
    }
}
