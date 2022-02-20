package com.cdprete.phonebook.authproxy.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory.NameConfig;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.singletonList;
import static reactor.core.publisher.Mono.defer;

/**
 * @author Cosimo Damiano Prete
 * @since 19/02/2022
 */
@Component
public class AuthenticationIdentityPropagatorGatewayFilterFactory extends AbstractGatewayFilterFactory<NameConfig> {
    AuthenticationIdentityPropagatorGatewayFilterFactory() {
        super(NameConfig.class);
    }

    @Override
    public GatewayFilter apply(NameConfig config) {
        return (exchange, chain) -> ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    var username = authentication.getName();
                    var updatedRequest = exchange.getRequest().mutate().header(config.getName(), username).build();
                    var updatedExchange = exchange.mutate().request(updatedRequest).build();

                    return chain.filter(updatedExchange);
                })
                .switchIfEmpty(defer(() -> chain.filter(exchange)));
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return singletonList(NAME_KEY);
    }
}
