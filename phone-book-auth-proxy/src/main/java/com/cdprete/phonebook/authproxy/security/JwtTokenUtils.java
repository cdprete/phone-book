package com.cdprete.phonebook.authproxy.security;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.PlainJWT;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * @author Cosimo Damiano Prete
 * @since 22/02/2022
 */
public class JwtTokenUtils {
    private static final String ROLE_CLAIM_NAME = "roles";

    private JwtTokenUtils() {
        throw new UnsupportedOperationException("Utility classes must not be instantiated");
    }

    public static JWT parseToken(String token) throws ParseException {
        Assert.hasText(token, "The token to parse cannot be blank");

        return PlainJWT.parse(token);
    }

    public static Set<String> extractRolesFromToken(JWT token) throws ParseException {
        Assert.notNull(token, "The token cannot be null");

        return Optional
                .ofNullable(token.getJWTClaimsSet().getStringArrayClaim(ROLE_CLAIM_NAME))
                .map(Set::of)
                .orElseGet(Collections::emptySet);
    }

    public static String extractSubjectFromToken(JWT token) throws ParseException {
        Assert.notNull(token, "The token cannot be null");

        return token.getJWTClaimsSet().getSubject();
    }
}
