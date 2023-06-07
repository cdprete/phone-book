package com.cdprete.phonebook.api.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

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
