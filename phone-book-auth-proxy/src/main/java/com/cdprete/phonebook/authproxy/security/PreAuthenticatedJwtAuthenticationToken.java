package com.cdprete.phonebook.authproxy.security;

import com.nimbusds.jwt.JWT;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.util.Set;

import static com.cdprete.phonebook.authproxy.security.JwtTokenUtils.extractSubjectFromToken;

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
    private final JWT jwt;

    public PreAuthenticatedJwtAuthenticationToken(Set<? extends GrantedAuthority> authorities, JWT token) {
        super(authorities);
        Assert.notNull(token, "The JWT token can't be null");

        // FIXME See Javadoc at the beginning on why the signature is not verified.
        jwt = token;
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
                return extractSubjectFromToken(jwt);
            } catch (ParseException e) {
                throw new BadCredentialsException(e.toString(), e);
            }
        };
    }
}
