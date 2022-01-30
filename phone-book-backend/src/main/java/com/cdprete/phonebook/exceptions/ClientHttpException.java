package com.cdprete.phonebook.exceptions;

/**
 * @author Cosimo Damiano Prete
 * @since 09/02/2022
 */
public abstract class ClientHttpException extends RuntimeException implements HttpException {
    ClientHttpException(String message) {
        super(message);
    }
}
