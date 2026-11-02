package com.patrick.personalfinance.security.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class JwtPropertiesTest {

    private static final String VALID_SECRET = "0123456789abcdef0123456789abcdef";

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class))
            .withUserConfiguration(PropertiesConfig.class)
            .withPropertyValues(
                    "jwt.expiration=24h",
                    "jwt.issuer=personal-finance-app"
            );

    @Test
    void bindsValidSecret() {
        runner.withPropertyValues("jwt.secret=" + VALID_SECRET)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context.getBean(JwtProperties.class).secret()).isEqualTo(VALID_SECRET);
                });
    }

    @Test
    void bindsExpirationAndIssuer() {
        runner.withPropertyValues(
                        "jwt.secret=" + VALID_SECRET,
                        "jwt.expiration=2h",
                        "jwt.issuer=another-issuer")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    JwtProperties properties = context.getBean(JwtProperties.class);
                    assertThat(properties.expiration()).isEqualTo(Duration.ofHours(2));
                    assertThat(properties.issuer()).isEqualTo("another-issuer");
                });
    }

    @Test
    void failsWhenSecretIsMissing() {
        runner.run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure())
                    .rootCause()
                    .hasMessageContaining("JWT_SECRET must be set");
        });
    }

    @Test
    void failsWhenSecretIsBlank() {
        runner.withPropertyValues("jwt.secret=")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .rootCause()
                            .hasMessageContaining("JWT_SECRET must be set");
                });
    }

    @Test
    void failsWhenSecretIsShorterThan32Characters() {
        runner.withPropertyValues("jwt.secret=" + "a".repeat(31))
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .rootCause()
                            .hasMessageContaining("at least 32 characters");
                });
    }

    @Test
    void failsWhenExpirationIsMissing() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class))
                .withUserConfiguration(PropertiesConfig.class)
                .withPropertyValues(
                        "jwt.secret=" + VALID_SECRET,
                        "jwt.issuer=personal-finance-app")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .rootCause()
                            .hasMessageContaining("jwt.expiration must be set");
                });
    }

    @Test
    void failsWhenExpirationIsShorterThanOneSecond() {
        runner.withPropertyValues("jwt.secret=" + VALID_SECRET, "jwt.expiration=0s")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .rootCause()
                            .hasMessageContaining("jwt.expiration must be at least 1 second");
                });
    }

    @Test
    void failsWhenIssuerIsBlank() {
        runner.withPropertyValues("jwt.secret=" + VALID_SECRET, "jwt.issuer=")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .rootCause()
                            .hasMessageContaining("jwt.issuer must be set");
                });
    }

    @Configuration
    @EnableConfigurationProperties(JwtProperties.class)
    static class PropertiesConfig {
    }
}
