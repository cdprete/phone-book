package com.cdprete.phonebook.utils.links;

import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import java.net.URI;

import static java.lang.String.format;

/**
 * @author Cosimo Damiano Prete
 * @since 05/02/2022
 */
public record Link(@NotNull Type type, @URL URI uri) {
    private static final String LINK_FORMAT = "<%s>; rel=\"%s\"";

    @Override
    public String toString() {
        return format(LINK_FORMAT, uri, type);
    }

    enum Type {
        first, previous, next, last
    }
}
