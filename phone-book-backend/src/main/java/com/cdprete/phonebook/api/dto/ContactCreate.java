package com.cdprete.phonebook.api.dto;

import javax.validation.Valid;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Cosimo Damiano Prete
 * @since 05/02/2022
 */
public class ContactCreate implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String surname;

    private List<@Valid EmailAddress> emailAddresses;
    private List<@Valid PhoneNumber> phoneNumbers;

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

    public List<EmailAddress> getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(List<EmailAddress> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}
