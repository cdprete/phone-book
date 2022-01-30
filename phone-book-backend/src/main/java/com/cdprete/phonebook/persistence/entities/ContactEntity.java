package com.cdprete.phonebook.persistence.entities;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import static javax.persistence.CascadeType.ALL;

/**
 * @author Cosimo Damiano Prete
 * @since 31/01/2022
 */
@Entity
@Table(name = "CONTACTS")
@EntityListeners(AuditingEntityListener.class)
public class ContactEntity extends AbstractPersistable<Long> {

    @Column(name = "NAME")
    private String name;

    @Column(name = "SURNAME")
    private String surname;

    @Lob
    @Column(name = "IMAGE")
    private byte[] image;

    @NotBlank
    @Size(min = 1, max = 36)
    @Column(name = "EXTERNAL_ID", length = 36, nullable = false, unique = true, updatable = false)
    private String externalId;

    @OneToMany(orphanRemoval = true, mappedBy = "contact", cascade = ALL)
    private List<EmailAddressEntity> emailAddresses;

    @OneToMany(orphanRemoval = true, mappedBy = "contact", cascade = ALL)
    private List<PhoneNumberEntity> phoneNumbers;

    @NotNull
    @CreatedDate
    @Column(name = "CREATION_DATE_TIME_UTC", nullable = false, updatable = false)
    private OffsetDateTime creationDateTime;

    @LastModifiedDate
    @Column(name = "LAST_UPDATE_DATE_TIME_UTC")
    private OffsetDateTime lastUpdateDateTime;

    @NotBlank
    @CreatedBy
    @Size(min = 1, max = 255)
    @Column(name = "CREATOR", nullable = false, updatable = false)
    private String creator;

    @LastModifiedBy
    @Size(max = 255)
    @Column(name = "LAST_UPDATER")
    private String lastUpdater;

    public ContactEntity() {
        emailAddresses = new LinkedList<>();
        phoneNumbers = new LinkedList<>();
    }

    @PrePersist
    private void populateExternalIdIfNeeded() {
        if(externalId == null) {
            externalId = UUID.randomUUID().toString();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getExternalId() {
        return externalId;
    }

    public List<EmailAddressEntity> getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(List<EmailAddressEntity> emailAddresses) {
        addItemsToCollection(this.emailAddresses, emailAddresses, EmailAddressEntity::setContact);
    }

    public List<PhoneNumberEntity> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumberEntity> phoneNumbers) {
        addItemsToCollection(this.phoneNumbers, phoneNumbers, PhoneNumberEntity::setContact);
    }

    public String getCreator() {
        return creator;
    }

    private <T, C extends Collection<T>> void addItemsToCollection(C target, C source, BiConsumer<T, ContactEntity> linker) {
        target.clear();
        if(source != null && !source.isEmpty()) {
            source.forEach(item -> {
                linker.accept(item, this);
                target.add(item);
            });
        }
    }

    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }
}
