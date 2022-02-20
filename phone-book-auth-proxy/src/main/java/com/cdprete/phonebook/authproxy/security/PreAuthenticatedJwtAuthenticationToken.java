package com.cdprete.phonebook.authproxy.security;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

import java.text.ParseException;

import static java.util.Collections.singleton;

/**
 * A pre-authenticated {@link Authentication} object which extracts the required information from the provided JWT token.
 *
 * The assumption in this demo is that if we reach this point we're then already authenticated and, since we have to
 * deal with just one role, it's not needed to extract it from the provided token.
 *
 * In the real world:
 * <ul>
 *     <li>Tokens are signed &rarr; therefore they need to be verified</li>
 *     <li>Tokens contain authentication information way more than just the username &rarr; they're therefore the first source of such information</li>
 *     <li>Tokens expire and such expiration should be checked</li>
 * </ul>
 *
 * @author Cosimo Damiano Prete
 * @since 19/02/2022
 */
class PreAuthenticatedJwtAuthenticationToken extends AbstractAuthenticationToken {
    // FIXME extract this from the token
    static final String EXPECTED_AUTHORITY = "USER";

    private final JWT jwt;

    public PreAuthenticatedJwtAuthenticationToken(String token) {
        // FIXME See Javadoc at the beginning on why this is hardcoded.
        super(singleton(new SimpleGrantedAuthority(EXPECTED_AUTHORITY)));
        Assert.hasText(token, "The JWT token can't be blank");

        // FIXME See Javadoc at the beginning on why the signature is not verified.
        try {
            jwt = SignedJWT.parse(token);
        } catch (ParseException e) {
            throw new BadCredentialsException(e.toString(), e);
        }
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public AuthenticatedPrincipal getPrincipal() {
        return () -> {
            try {
                return jwt.getJWTClaimsSet().getSubject();
            } catch (ParseException e) {
                throw new BadCredentialsException(e.toString(), e);
            }
        };
    }
}
