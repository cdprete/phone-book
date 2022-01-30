package com.cdprete.phonebook.api.service;

import com.cdprete.phonebook.api.dto.BasicContactRead;
import com.cdprete.phonebook.api.dto.ContactCreate;
import com.cdprete.phonebook.api.dto.ContactRead;
import com.cdprete.phonebook.api.dto.ContactUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Cosimo Damiano Prete
 * @since 02/02/2022
 */
public interface ContactService {
    @NotNull
    Page<@Valid BasicContactRead> readContacts(@NotNull Pageable pageable, String searchValue);

    @Valid
    @NotNull
    ContactRead readSingleContact(@NotBlank String id);

    @NotBlank
    String createContact(@NotNull @Valid ContactCreate contactData, @Valid Image image);

    @NotBlank
    String createContact(@NotNull @Valid ContactCreate contactData);

    void updateContact(@NotBlank String id, @NotNull @Valid ContactUpdate data, @Valid Image image);

    void updateContact(@NotBlank String id, @NotNull @Valid ContactUpdate data);

    void deleteContact(@NotBlank String id);

    record Image(@NotNull @NotEmpty byte[] bytes, @NotBlank String mediaType) {
        public Image(byte[] bytes, String mediaType) {
            this.bytes = bytes;
            this.mediaType = mediaType;
        }
    }
}