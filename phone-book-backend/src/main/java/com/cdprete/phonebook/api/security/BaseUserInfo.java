package com.cdprete.phonebook.api.security;

import jakarta.validation.constraints.NotBlank;

/**
 * @author Cosimo Damiano Prete
 * @since 11/02/2022
 */
public class BaseUserInfo extends UserInfoAdapter {
    private final String username;

    public BaseUserInfo(String username) {
        this.username = username;
    }

    @NotBlank
    @Override
    public String getUsername() {
        return username;
    }
}
