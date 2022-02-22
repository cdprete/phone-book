package com.cdprete.phonebook.idp.service;

import com.cdprete.phonebook.idp.api.service.AuthService;
import com.cdprete.phonebook.idp.exceptions.UsernameAlreadyRegisteredException;
import com.cdprete.phonebook.idp.persistence.entities.RoleEntity;
import com.cdprete.phonebook.idp.persistence.entities.UserEntity;
import com.cdprete.phonebook.idp.persistence.repository.UserRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.cdprete.phonebook.idp.security.JwtTokenFactory.buildJwtForUser;
import static com.cdprete.phonebook.idp.security.JwtTokenFactory.serializeJwt;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;
import static org.springframework.context.annotation.ScopedProxyMode.INTERFACES;

/**
 * @author Cosimo Damiano Prete
 * @since 20/02/2022
 */
@Service
@Scope(proxyMode = INTERFACES)
@Transactional(readOnly = true)
class DefaultAuthService implements AuthService {
    private static final String USER_AUTHORITY = "USER";

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;

    DefaultAuthService(PasswordEncoder passwordEncoder, UserRepository repository) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
    }

    @Override
    public String login(String username, String password) {
        var exception = new BadCredentialsException("Login failed with the provided credentials");
        var existingCredentials = repository.findByUsername(username).orElseThrow(() -> exception);
        if(!passwordEncoder.matches(password, existingCredentials.getPassword())) {
            throw exception;
        }

        return serializeJwt(buildJwtForUser(username, existingCredentials.getRoles().stream().map(RoleEntity::getRole).collect(toSet())));
    }

    @Override
    @Transactional
    public void register(String username, String password) {
        if(repository.existsByUsername(username)) {
            throw new UsernameAlreadyRegisteredException(username);
        }

        var entity = new UserEntity();
        entity.setUsername(username);
        entity.setPassword(passwordEncoder.encode(password));
        entity.setRoles(singleton(new RoleEntity(USER_AUTHORITY)));

        repository.save(entity);
    }
}
