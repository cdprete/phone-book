package com.cdprete.phonebook.idp.exceptions;

import org.springframework.http.HttpStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * @author Cosimo Damiano Prete
 * @since 20/02/2022
 */
public class UsernameAlreadyRegisteredException extends IllegalArgumentException {
    public UsernameAlreadyRegisteredException(@NotBlank String username) {
        super(format("The is already a user registered with the username '%s'", username));
    }

    @NotBlank
    public String getClientCode() {
        return "username.already.registered";
    }

    @NotNull
    public HttpStatus statusCode() {
        return CONFLICT;
    }
}
