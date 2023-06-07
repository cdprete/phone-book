package com.cdprete.phonebook.exceptions;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;

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
