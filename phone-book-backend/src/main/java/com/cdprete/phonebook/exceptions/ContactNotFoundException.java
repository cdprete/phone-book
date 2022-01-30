package com.cdprete.phonebook.exceptions;

import static java.lang.String.format;

/**
 * @author Cosimo Damiano Prete
 * @since 08/02/2022
 */
public class ContactNotFoundException extends NotFoundHttpException {
    public ContactNotFoundException(String id) {
        super(format("Contact with ID '%s' not found.", id));
    }

    @Override
    public String clientCode() {
        return "contact.not.found";
    }
}
