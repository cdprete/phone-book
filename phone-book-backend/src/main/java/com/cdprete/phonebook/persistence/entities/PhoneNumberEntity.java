package com.cdprete.phonebook.persistence.entities;

import com.cdprete.phonebook.api.dto.PhoneNumber.Type;
import com.cdprete.phonebook.persistence.entities.PhoneNumberEntity.PhoneNumberEntityId;
import com.cdprete.phonebook.utils.validation.TelephoneNumber;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

import static javax.persistence.FetchType.LAZY;

/**
 * @author Cosimo Damiano Prete
 * @since 31/01/2022
 */
@Entity
@IdClass(PhoneNumberEntityId.class)
@Table(name = "CONTACT_PHONE_NUMBERS")
public class PhoneNumberEntity {
    @Id
    @TelephoneNumber
    @Size(min = 1, max = 15)
    @Column(name = "PHONE_NUMBER", nullable = false, length = 15)
    private String phoneNumber;

    @Id
    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "CONTACT_ID", nullable = false, insertable = false, updatable = false)
    private ContactEntity contact;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", length = 10, nullable = false)
    private Type type;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String emailAddress) {
        this.phoneNumber = emailAddress;
    }

    public ContactEntity getContact() {
        return contact;
    }

    public void setContact(ContactEntity contact) {
        this.contact = contact;
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
        PhoneNumberEntity that = (PhoneNumberEntity) o;
        return phoneNumber.equals(that.phoneNumber) && contact.equals(that.contact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phoneNumber, contact);
    }

    static class PhoneNumberEntityId implements Serializable {
        private ContactEntity contact;
        private String phoneNumber;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PhoneNumberEntityId that = (PhoneNumberEntityId) o;
            return contact.equals(that.contact) && phoneNumber.equals(that.phoneNumber);
        }

        @Override
        public int hashCode() {
            return Objects.hash(contact, phoneNumber);
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", PhoneNumberEntityId.class.getSimpleName() + "[", "]")
                    .add("contact=" + contact)
                    .add("phoneNumber='" + phoneNumber + "'")
                    .toString();
        }
    }
}
