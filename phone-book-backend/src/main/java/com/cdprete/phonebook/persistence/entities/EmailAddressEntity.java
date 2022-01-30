package com.cdprete.phonebook.persistence.entities;

import com.cdprete.phonebook.api.dto.EmailAddress.Type;
import com.cdprete.phonebook.persistence.entities.EmailAddressEntity.EmailAddressEntityId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

import static javax.persistence.FetchType.LAZY;

/**
 * @author Cosimo Damiano Prete
 * @since 31/01/2022
 */
@Entity
@IdClass(EmailAddressEntityId.class)
@Table(name = "CONTACT_EMAIL_ADDRESSES")
public class EmailAddressEntity {
    @Id
    @Email
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "EMAIL_ADDRESS", nullable = false)
    private String emailAddress;

    @Id
    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "CONTACT_ID", nullable = false, insertable = false, updatable = false)
    private ContactEntity contact;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", length = 10, nullable = false)
    private Type type;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
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
        EmailAddressEntity that = (EmailAddressEntity) o;
        return emailAddress.equals(that.emailAddress) && contact.equals(that.contact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailAddress, contact);
    }

    static class EmailAddressEntityId implements Serializable {
        private ContactEntity contact;
        private String emailAddress;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EmailAddressEntityId that = (EmailAddressEntityId) o;
            return contact.equals(that.contact) && emailAddress.equals(that.emailAddress);
        }

        @Override
        public int hashCode() {
            return Objects.hash(contact, emailAddress);
        }
    }
}
