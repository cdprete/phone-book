package com.cdprete.phonebook.api.dto;

import com.cdprete.phonebook.utils.validation.TelephoneNumber;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

import static java.lang.String.format;

/**
 * @author Cosimo Damiano Prete
 * @since 02/02/2022
 */
public class PhoneNumber implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TelephoneNumber
    private String phoneNumber;

    @NotNull
    private Type type;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
        PhoneNumber that = (PhoneNumber) o;
        return phoneNumber.equals(that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return phoneNumber.hashCode();
    }

    @Override
    public String toString() {
        return format("Phone number %s of type %s", phoneNumber, type);
    }

    public enum Type {
        MOBILE, HOME, OFFICE, OTHER
    }
}
