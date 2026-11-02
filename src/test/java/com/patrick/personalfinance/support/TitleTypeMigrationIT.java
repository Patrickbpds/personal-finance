package com.patrick.personalfinance.support;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TitleTypeMigrationIT extends PostgresIntegrationTest {

    private static final String DATABASE = "title_type_migration";

    @Test
    @DisplayName("V7 converts legacy ordinal types into enum names")
    void convertsLegacyOrdinalsToEnumNames() throws Exception {
        String databaseUrl = freshDatabaseMigratedTo("6");

        UUID userId = UUID.randomUUID();
        UUID receivableId = UUID.randomUUID();
        UUID payableId = UUID.randomUUID();

        try (Connection connection = connect(databaseUrl)) {
            insertUser(connection, userId);
            insertTitle(connection, receivableId, userId, 0);
            insertTitle(connection, payableId, userId, 1);
        }

        migrate(databaseUrl, "latest");

        try (Connection connection = connect(databaseUrl)) {
            assertThat(typeOf(connection, receivableId)).isEqualTo("RECEIVABLE");
            assertThat(typeOf(connection, payableId)).isEqualTo("PAYABLE");
            assertThat(typeColumnType(connection)).isEqualTo("character varying");
        }
    }

    @Test
    @DisplayName("V7 keeps the type restricted to the known enum names")
    void rejectsValuesOutsideTheEnum() throws Exception {
        String databaseUrl = freshDatabaseMigratedTo("latest");
        UUID userId = UUID.randomUUID();

        try (Connection connection = connect(databaseUrl)) {
            insertUser(connection, userId);

            assertThatThrownBy(() -> insertTitle(connection, UUID.randomUUID(), userId, "UNKNOWN"))
                    .isInstanceOf(SQLException.class)
                    .hasMessageContaining("chk_title_type_valid");
        }
    }

    private String freshDatabaseMigratedTo(String version) throws Exception {
        recreateDatabase();
        String databaseUrl = databaseUrl(DATABASE);
        migrate(databaseUrl, version);
        return databaseUrl;
    }

    private void recreateDatabase() throws Exception {
        try (Connection admin = connect(databaseUrl("postgres"));
             Statement statement = admin.createStatement()) {
            statement.execute("DROP DATABASE IF EXISTS " + DATABASE + " WITH (FORCE)");
            statement.execute("CREATE DATABASE " + DATABASE);
        }
    }

    private void migrate(String url, String version) {
        var configuration = Flyway.configure()
                .dataSource(url, POSTGRES.getUsername(), POSTGRES.getPassword())
                .cleanDisabled(false);

        if (!"latest".equals(version)) {
            configuration.target(MigrationVersion.fromVersion(version));
        }

        configuration.load().migrate();
    }

    private Connection connect(String url) throws SQLException {
        return DriverManager.getConnection(url, POSTGRES.getUsername(), POSTGRES.getPassword());
    }

    private void insertUser(Connection connection, UUID userId) throws SQLException {
        try (var statement = connection.prepareStatement(
                "INSERT INTO finance_tables.user (id_user, name, email, password) VALUES (?, ?, ?, ?)")) {
            statement.setObject(1, userId);
            statement.setString(2, "Migration User");
            statement.setString(3, "migration-" + userId + "@example.com");
            statement.setString(4, "encoded-password");
            statement.executeUpdate();
        }
    }

    private void insertTitle(Connection connection, UUID titleId, UUID userId, Object type) throws SQLException {
        try (var statement = connection.prepareStatement("""
                INSERT INTO finance_tables.title
                    (idtitle, description, iduser, type, value, expirationdate)
                VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """)) {
            statement.setObject(1, titleId);
            statement.setString(2, "Migration title");
            statement.setObject(3, userId);
            statement.setObject(4, type);
            statement.setBigDecimal(5, BigDecimal.TEN);
            statement.executeUpdate();
        }
    }

    private String typeOf(Connection connection, UUID titleId) throws SQLException {
        try (var statement = connection.prepareStatement(
                "SELECT type FROM finance_tables.title WHERE idtitle = ?")) {
            statement.setObject(1, titleId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getString(1);
            }
        }
    }

    private String typeColumnType(Connection connection) throws SQLException {
        try (var statement = connection.prepareStatement("""
                SELECT data_type
                FROM information_schema.columns
                WHERE table_schema = 'finance_tables'
                  AND table_name = 'title'
                  AND column_name = 'type'
                """)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getString(1);
            }
        }
    }

    private String databaseUrl(String database) {
        return POSTGRES.getJdbcUrl().replaceAll("/[^/]+$", "/" + database);
    }
}
