package com.cdprete.phonebook.idp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;

import static java.time.Duration.ofMinutes;
import static org.springframework.http.HttpHeaders.ORIGIN;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * @author Cosimo Damiano Prete
 * @since 22/02/2022
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Disable the whole security which is automatically initiated since we add the PasswordEncoder to the context.
        http
                .httpBasic().disable()
                .cors().configurationSource(request -> {
                    // CORS is by default not allowed, which would not allow us to run the UI locally and contact this
                    // server. With this configuration we're enabling it, but a bit stricter than the default "safe"
                    // config provided by Spring Boot at org.springframework.web.cors.CorsConfiguration.applyPermitDefaultValues
                    var config = new CorsConfiguration();
                    config.setAllowCredentials(true);
                    // In the IdP we've only POST requests
                    config.addAllowedMethod(POST);
                    config.addAllowedOrigin(request.getHeader(ORIGIN));
                    config.setMaxAge(ofMinutes(30));
                    request.getHeaderNames().asIterator().forEachRemaining(config::addAllowedHeader);

                    return config;
                }).and()
                .csrf().disable()
                .formLogin().disable()
                .logout().disable()
                // Allow frames (for H2 Web console)
                .headers().frameOptions().disable().and()
                .sessionManagement().sessionCreationPolicy(STATELESS).and()
                .authorizeRequests().anyRequest().permitAll();
    }
}
