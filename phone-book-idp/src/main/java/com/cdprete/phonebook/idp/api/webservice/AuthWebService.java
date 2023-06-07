package com.cdprete.phonebook.idp.api.webservice;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.validation.constraints.NotBlank;

import static com.cdprete.phonebook.idp.utils.Constants.API_VERSION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

/**
 * @author Cosimo Damiano Prete
 * @since 20/02/2022
 */
@RequestMapping(API_VERSION)
public interface AuthWebService {
    @NotBlank
    @PostMapping(value = "/login", consumes = MULTIPART_FORM_DATA_VALUE)
    String login(@RequestPart @NotBlank String username, @RequestPart @NotBlank String password);

    @ResponseStatus(CREATED)
    @PostMapping(value = "/register", consumes = MULTIPART_FORM_DATA_VALUE)
    void register(@RequestPart @NotBlank String username, @RequestPart @NotBlank String password);
}
