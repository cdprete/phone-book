package com.cdprete.phonebook.idp.security;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Date.from;

/**
 * @author Cosimo Damiano Prete
 * @since 20/02/2022
 */
public class JwtTokenFactory {
    private static final String ROLES_CLAIM_NAME = "roles";

    private static final Supplier<Date> tokenExpirationTimeGenerator = () -> from(now().plus(10, MINUTES));

    private JwtTokenFactory() {}

    public static JWT buildJwtForUser(@NotBlank String username, Set<String> roles) {
        var claims = new JWTClaimsSet.Builder()
                .subject(username)
                .claim(ROLES_CLAIM_NAME, Optional.ofNullable(roles).orElseGet(Collections::emptySet))
                .expirationTime(tokenExpirationTimeGenerator.get())
                .build();
        return new PlainJWT(claims);
    }

    public static String serializeJwt(@NotNull JWT jwt) {
        return jwt.serialize();
    }
}
