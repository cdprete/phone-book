package com.cdprete.phonebook.idp.webservice;

import com.cdprete.phonebook.idp.api.service.AuthService;
import com.cdprete.phonebook.idp.api.webservice.AuthWebService;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Cosimo Damiano Prete
 * @since 20/02/2022
 */
@RestController
class DefaultAuthWebService implements AuthWebService {
    private final AuthService service;

    DefaultAuthWebService(AuthService service) {
        this.service = service;
    }

    @Override
    public String login(String username, String password) {
        return service.login(username, password);
    }

    @Override
    public void register(String username, String password) {
        service.register(username, password);
    }
}
