package com.cdprete.phonebook.exceptions;

/**
 * @author Cosimo Damiano Prete
 * @since 08/02/2022
 */
public class MissingNameAndSurnameException extends BadRequestHttpException {
    public MissingNameAndSurnameException() {
        super("The name and/or the surname must have a value. They cannot both be blank.");
    }

    @Override
    public String clientCode() {
        return "name.and.surname.blank";
    }
}
