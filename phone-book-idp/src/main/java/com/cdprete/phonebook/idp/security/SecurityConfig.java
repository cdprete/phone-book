package com.cdprete.phonebook.idp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static java.time.Duration.ofMinutes;
import static org.springframework.context.annotation.ScopedProxyMode.INTERFACES;
import static org.springframework.http.HttpHeaders.ORIGIN;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * @author Cosimo Damiano Prete
 * @since 22/02/2022
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired(required = false)
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Disable the whole security which is automatically initiated since we add the PasswordEncoder to the context.
        http.httpBasic(AbstractHttpConfigurer::disable);
        if (corsConfigurationSource == null) {
            http.cors(AbstractHttpConfigurer::disable);
        } else {
            http.cors(c -> c.configurationSource(corsConfigurationSource));
        }
        http
                .csrf(CsrfConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable)
                .logout(LogoutConfigurer::disable)
                // Allow frame(for H2 Web console)
                .headers(hc -> hc.frameOptions(FrameOptionsConfig::disable))
                .sessionManagement(smc -> smc.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(rc -> rc.anyRequest().permitAll());

        return http.build();
    }

    @Bean
    @Profile("dev")
    @Scope(proxyMode = INTERFACES)
    CorsConfigurationSource devCorsConfiguration() {
        // CORS is by default not allowed, which would not allow us to run the UI locally and contact this server.
        // With this configuration we're enabling it, but a bit stricter than the default "safe" config provided by
        // Spring Boot at org.springframework.web.cors.CorsConfiguration.applyPermitDefaultValues
        return request -> {
            var config = new CorsConfiguration();
            config.setAllowCredentials(true);
            // In the IdP we've only POST requests
            config.addAllowedMethod(POST);
            config.addAllowedOrigin(request.getHeader(ORIGIN));
            config.setMaxAge(ofMinutes(30));
            request.getHeaderNames().asIterator().forEachRemaining(config::addAllowedHeader);

            return config;
        };
    }
}
