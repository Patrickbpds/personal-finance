package com.patrick.personalfinance;

import com.patrick.personalfinance.support.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that the application context starts against a real PostgreSQL
 * instance and that Flyway applied every migration, leaving the schema ready
 * for the remaining integration tests.
 */
@SpringBootTest
class PersonalFinanceApplicationTests extends PostgresIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void flywayMigrationsCreateExpectedSchema() {
        Integer appliedMigrations = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM finance_tables.flyway_schema_history",
                Integer.class
        );
        assertThat(appliedMigrations).isNotNull().isGreaterThanOrEqualTo(6);

        Integer titleTableExists = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables "
                        + "WHERE table_schema = 'finance_tables' AND table_name = 'title'",
                Integer.class
        );
        assertThat(titleTableExists).isEqualTo(1);
    }
}
