package com.akash.embedqa.audit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * Author: akash
 * Date: 29/10/25
 */

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditAwareImpl {

    @Bean
    public AuditorAware<String> auditorProvider() {
        // For now, return a default user.
        // Later, integrate with Spring Security to get actual authenticated user
        return () -> Optional.of("system");
    }
}
