package com.cdprete.phonebook.authproxy.security;

import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static com.cdprete.phonebook.authproxy.security.PreAuthenticatedJwtAuthenticationToken.EXPECTED_AUTHORITY;
import static org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHENTICATION;

/**
 * @author Cosimo Damiano Prete
 * @since 16/02/2022
 */
@Configuration
class SecurityConfig {
    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, GatewayProperties gatewayProperties) {
        http
                .httpBasic().disable()
                .cors().disable()
                .csrf().disable()
                .formLogin().disable()
                .logout().disable()
                .addFilterBefore(new PreAuthenticatedJwtWebFilter(), AUTHENTICATION)
                .authorizeExchange()
                // Allow the access to the health endpoint for the readiness and liveness checks
                .matchers(EndpointRequest.to(HealthEndpoint.class)).permitAll()
                // TODO make this properly configurable
                .pathMatchers(getAllPaths(gatewayProperties)).hasAuthority(EXPECTED_AUTHORITY)
                .anyExchange().permitAll();

        return http.build();
    }

    private static String[] getAllPaths(GatewayProperties props) {
        return props.getRoutes().stream().flatMap(r ->
                r.getPredicates().stream().flatMap(p ->
                        p.getArgs().values().stream())).toArray(String[]::new);
    }
}
