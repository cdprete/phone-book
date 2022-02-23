package com.cdprete.phonebook.authproxy.security;

import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.util.Assert;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHENTICATION;

/**
 * @author Cosimo Damiano Prete
 * @since 16/02/2022
 */
@Configuration
@EnableWebFluxSecurity
class SecurityConfig {
    private static final String EXPECTED_AUTHORITY = "USER";

    private static final String BACKEND_CORE_API_ROUTE_ID = "backend-core";
    private static final String BACKEND_ACTUATOR_API_ROUTE_ID = "backend-actuator";

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, GatewayProperties gatewayProperties, PathRoutePredicateFactory pathRoutePredicateFactory) {
        http
                // Basic Auth is always prohibited
                .httpBasic().authenticationEntryPoint(new HttpStatusServerEntryPoint(UNAUTHORIZED)).and()
                .cors().disable()
                .csrf().disable()
                .formLogin().disable()
                .logout().disable()
                .addFilterAfter(new PreAuthenticatedJwtWebFilter(), AUTHENTICATION)
                .authorizeExchange()
                // TODO make these two properly configurable
                .pathMatchers(getAllPathsForRoute(gatewayProperties, pathRoutePredicateFactory.name(), BACKEND_CORE_API_ROUTE_ID)).hasAuthority(EXPECTED_AUTHORITY)
                .pathMatchers(getAllPathsForRoute(gatewayProperties, pathRoutePredicateFactory.name(), BACKEND_ACTUATOR_API_ROUTE_ID)).permitAll()
                // Allow the access to the health endpoint for the readiness and liveness checks
                .matchers(EndpointRequest.to(HealthEndpoint.class)).permitAll()
                .anyExchange().permitAll();

        return http.build();
    }

    private static String[] getAllPathsForRoute(GatewayProperties props, String filterName, String routeId) {
        var paths = props.getRoutes().stream().filter(r -> r.getId().equals(routeId)).flatMap(r ->
                r.getPredicates().stream().filter(p -> p.getName().equals(filterName)).flatMap(p ->
                        p.getArgs().values().stream())).toArray(String[]::new);
        Assert.state(paths.length > 0, format("""
                        No paths have been found for the route ID '%s'.\
                        Please check the configuration so that the expected paths are mapped under the specified route ID
                        """, routeId));

        return paths;
    }
}
