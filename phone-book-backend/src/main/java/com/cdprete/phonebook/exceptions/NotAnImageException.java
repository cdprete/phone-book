package com.cdprete.phonebook.exceptions;

import static java.lang.String.format;

/**
 * @author Cosimo Damiano Prete
 * @since 08/02/2022
 */
public class NotAnImageException extends BadRequestHttpException {
    public NotAnImageException(String mediaType) {
        super(format("The media type '%s' of the uploaded bytes is not the one of an image.", mediaType));
    }

    @Override
    public String clientCode() {
        return "not.an.image";
    }
}
