package com.cdprete.phonebook.authproxy.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.text.ParseException;

import static com.cdprete.phonebook.authproxy.security.JwtTokenUtils.extractRolesFromToken;
import static com.cdprete.phonebook.authproxy.security.JwtTokenUtils.parseToken;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.core.context.ReactiveSecurityContextHolder.withSecurityContext;
import static reactor.core.publisher.Mono.defer;
import static reactor.core.publisher.Mono.empty;
import static reactor.core.publisher.Mono.just;

/**
 * @author Cosimo Damiano Prete
 * @since 19/02/2022
 */
class PreAuthenticatedJwtWebFilter implements WebFilter {
    private static final Logger logger = LoggerFactory.getLogger(PreAuthenticatedJwtWebFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var request = exchange.getRequest();
        if(!request.getHeaders().containsKey(AUTHORIZATION)) {
            logger.debug("The request doesn't contain the {} header. It will therefore be executed without authentication", AUTHORIZATION);
            return chain.filter(exchange);
        }

        var token = request.getHeaders().getFirst(AUTHORIZATION);
        return ReactiveSecurityContextHolder.getContext().switchIfEmpty(defer(() -> {
            try {
                var jwt = parseToken(token);
                var authorities = extractRolesFromToken(jwt).stream().map(SimpleGrantedAuthority::new).collect(toUnmodifiableSet());
                var authentication = new PreAuthenticatedJwtAuthenticationToken(authorities, jwt);
                var securityContext = new SecurityContextImpl(authentication);

                return chain.filter(exchange)
                        .contextWrite(context -> context.putAll(withSecurityContext(just(securityContext)).readOnly()))
                        .then(empty());
            } catch (ParseException e) {
                throw new BadCredentialsException(e.toString(), e);
            }
        })).flatMap((securityContext) -> chain.filter(exchange));
    }
}
