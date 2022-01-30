package com.cdprete.phonebook.api.dto;

import javax.validation.Valid;
import java.io.Serial;
import java.util.Set;

/**
 * @author Cosimo Damiano Prete
 * @since 02/02/2022
 */
public class ContactRead extends BasicContactRead {
    @Serial
    private static final long serialVersionUID = 1L;

    private Set<@Valid EmailAddress> emailAddresses;
    private Set<@Valid PhoneNumber> phoneNumbers;

    public Set<EmailAddress> getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(Set<EmailAddress> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    public Set<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Set<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}
