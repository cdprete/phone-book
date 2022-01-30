package com.cdprete.phonebook.api.webservice;

import com.cdprete.phonebook.api.dto.SettingsRead;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.cdprete.phonebook.utils.Constants.API_VERSION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Cosimo Damiano Prete
 * @since 13/02/2022
 */
@RequestMapping(API_VERSION + "/settings")
public interface SettingsWebService {
    @Valid
    @NotNull
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    SettingsRead getSettings();
}
