package com.cdprete.phonebook.api.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

import static java.lang.String.format;

/**
 * @author Cosimo Damiano Prete
 * @since 02/02/2022
 */
public class EmailAddress implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Email
    @NotNull
    private String emailAddress;
    @NotNull
    private Type type;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailAddress that = (EmailAddress) o;
        return emailAddress.equals(that.emailAddress);
    }

    @Override
    public int hashCode() {
        return emailAddress.hashCode();
    }

    @Override
    public String toString() {
        return format("E-mail address %s of type %s", emailAddress, type);
    }

    public enum Type {
        HOME, OFFICE, OTHER
    }
}
