package com.cdprete.phonebook.idp.api.service;

import javax.validation.constraints.NotBlank;

/**
 * @author Cosimo Damiano Prete
 * @since 20/02/2022
 */
public interface AuthService {
    @NotBlank
    String login(@NotBlank String username, @NotBlank String password);

    void register(@NotBlank String username, @NotBlank String password);
}
