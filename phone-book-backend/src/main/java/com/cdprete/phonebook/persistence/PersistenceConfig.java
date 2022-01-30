package com.cdprete.phonebook.persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Optional;

import static com.cdprete.phonebook.security.SecurityContextHolder.getUserInfo;
import static java.time.Clock.systemUTC;
import static java.time.OffsetDateTime.now;

/**
 * @author Cosimo Damiano Prete
 * @since 31/01/2022
 */
@Configuration
@EnableTransactionManagement
@EnableJpaAuditing(
        auditorAwareRef = PersistenceConfig.AUDITOR_AWARE_BEAN_NAME,
        dateTimeProviderRef = PersistenceConfig.DATE_TIME_PROVIDER_BEAN_NAME
)
public class PersistenceConfig {
    static final String AUDITOR_AWARE_BEAN_NAME = "auditorAware";
    static final String DATE_TIME_PROVIDER_BEAN_NAME = "dateTimeProvider";

    @Bean(name = AUDITOR_AWARE_BEAN_NAME)
    AuditorAware<String> auditorAware() {
        return () -> Optional.of(getUserInfo().getUsername());
    }

    @Bean(name = DATE_TIME_PROVIDER_BEAN_NAME)
    DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(now(systemUTC()));
    }
}
