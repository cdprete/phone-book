package com.cdprete.phonebook.exceptions;

import java.util.Set;

import static java.lang.String.format;
import static java.lang.String.join;

/**
 * @author Cosimo Damiano Prete
 * @since 08/02/2022
 */
public class DuplicateEmailAddressesException extends BadRequestHttpException {
    public DuplicateEmailAddressesException(Set<String> duplicates) {
        super(format("Duplicates e-mail addresses are unsupported. The following duplicates were found: %s", join(",", duplicates)));
    }

    @Override
    public String clientCode() {
        return "duplicate.email.addresses";
    }
}
