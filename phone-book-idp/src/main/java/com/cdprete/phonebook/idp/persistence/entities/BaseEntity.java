package com.cdprete.phonebook.idp.persistence.entities;

import org.springframework.data.domain.Persistable;

import javax.persistence.Transient;
import java.util.Objects;

/**
 * @author Cosimo Damiano Prete
 * @since 20/02/2022
 */
abstract class BaseEntity implements Persistable<String> {
    @Override
    @Transient
    public boolean isNew() {
        return getId() == null;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return getId().equals(that.getUsername());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public final String toString() {
        return String.format("Entity of type %s with id: %s", getClass().getName(), getId());
    }
}
