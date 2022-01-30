package com.cdprete.phonebook.exceptions;

import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Cosimo Damiano Prete
 * @since 08/02/2022
 */
public interface HttpException {
    @NotNull
    HttpStatus statusCode();

    @NotBlank
    String clientCode();
}
