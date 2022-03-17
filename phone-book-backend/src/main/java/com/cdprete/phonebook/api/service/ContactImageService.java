package com.cdprete.phonebook.api.service;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Cosimo Damiano Prete
 * @since 17/03/2022
 */
public interface ContactImageService {
    void uploadContactImage(@NotBlank String id, @Valid Image image);

    record Image(@NotNull @NotEmpty byte[] bytes, @NotBlank String mediaType) {
        public Image(byte[] bytes, String mediaType) {
            this.bytes = bytes;
            this.mediaType = mediaType;
        }
    }
}
