package com.patrick.personalfinance.support;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Base class for integration tests backed by a real PostgreSQL database.
 *
 * <p>A single PostgreSQL 17 container is shared across the whole test suite
 * (static field + {@link ServiceConnection}) so migrations run once and every
 * test class can rely on the same migrated schema.</p>
 *
 * <p>The container requires a reachable Docker daemon. If the daemon socket is
 * only available to the {@code docker} group, run the build through that group
 * (for example {@code sg docker -c './mvnw verify'}) - see the "Running the
 * tests" section of the README.</p>
 *
 * <p>{@link DynamicPropertySource} also provides a test-only {@code JWT_SECRET}
 * so the application context can start without depending on a real environment.</p>
 */
public abstract class PostgresIntegrationTest {

    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void registerTestProperties(DynamicPropertyRegistry registry) {
        registry.add("JWT_SECRET", () -> "test-secret-key-with-at-least-32-characters");
        registry.add("app.cors.allowed-origins", () -> "http://localhost:4200");
    }
}
