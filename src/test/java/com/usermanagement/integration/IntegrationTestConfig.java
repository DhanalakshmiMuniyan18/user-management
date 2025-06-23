package com.usermanagement.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ActiveProfiles;
import com.usermanagement.config.AuditingConfig;
import org.springframework.context.annotation.Import;

import java.util.Optional;

/**
 * @author Saravanamuthukumar S
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(AuditingConfig.class)
public abstract class IntegrationTestConfig {

    @TestConfiguration
    static class TestAuditingConfig {
        @Bean
        @Primary
        public AuditorAware<String> testAuditorProvider() {
            return () -> Optional.of("test-user");
        }
    }
} 