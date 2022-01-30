package com.cdprete.phonebook.exceptions;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * @author Cosimo Damiano Prete
 * @since 08/02/2022
 */
public abstract class NotFoundHttpException extends ClientHttpException {
    public NotFoundHttpException(String message) {
        super(message);
    }

    @Override
    public final HttpStatus statusCode() {
        return NOT_FOUND;
    }
}
