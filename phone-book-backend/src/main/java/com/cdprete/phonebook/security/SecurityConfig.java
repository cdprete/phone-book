package com.cdprete.phonebook.security;

import com.cdprete.phonebook.api.security.UserInfo;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

import static org.springframework.context.annotation.ScopedProxyMode.INTERFACES;

/**
 * @author Cosimo Damiano Prete
 * @since 11/02/2022
 */
@Configuration
class SecurityConfig {
    @Bean
    @RequestScope(proxyMode = INTERFACES)
    UserInfo userInfo() {
        return SecurityContextHolder.getUserInfo();
    }

    @Bean
    FilterRegistrationBean<UserInfoExtractorFilter> userInfoExtractorFilterFilterBean(SecurityContextHolder securityContextHolder) {
        return new FilterRegistrationBean<>(new UserInfoExtractorFilter(securityContextHolder));
    }
}
