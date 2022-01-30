package com.cdprete.phonebook.api.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Cosimo Damiano Prete
 * @since 02/02/2022
 */
abstract class Identifiable implements Serializable {
    @NotBlank
    private String id;

    public final String getId() {
        return id;
    }

    public final void setId(String id) {
        this.id = id;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifiable that = (Identifiable) o;
        return id.equals(that.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }
}
