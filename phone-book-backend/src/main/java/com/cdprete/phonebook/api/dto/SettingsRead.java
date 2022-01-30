package com.cdprete.phonebook.api.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Cosimo Damiano Prete
 * @since 13/02/2022
 */
public record SettingsRead(long maxImageSizeBytes) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
