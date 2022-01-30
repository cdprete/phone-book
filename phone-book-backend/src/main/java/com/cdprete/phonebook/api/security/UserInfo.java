package com.cdprete.phonebook.api.security;

/**
 * @author Cosimo Damiano Prete
 * @since 11/02/2022
 */
public interface UserInfo {
    String getUsername();

    boolean isAnonymous();
}
