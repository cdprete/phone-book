package com.cdprete.phonebook.webservice;

import com.cdprete.phonebook.api.dto.SettingsRead;
import com.cdprete.phonebook.api.webservice.SettingsWebService;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Cosimo Damiano Prete
 * @since 13/02/2022
 */
@RestController
public class DefaultSettingsWebService implements SettingsWebService {
    private final MultipartProperties multipartProperties;

    public DefaultSettingsWebService(MultipartProperties multipartProperties) {
        this.multipartProperties = multipartProperties;
    }

    @Override
    public SettingsRead getSettings() {
        return new SettingsRead(multipartProperties.getMaxFileSize().toBytes());
    }
}
