package ua.yelisieiev.service;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SecurityServiceTest {

    private SecurityService securityService;

    @BeforeEach
    void setUp() throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        String[] bdServers = {"instances.spawn.cc"};
        dataSource.setServerNames(bdServers);
        int[] bdPorts = {32213};
        dataSource.setPortNumbers(bdPorts);
        dataSource.setDatabaseName("foobardb");
        dataSource.setUser("spawn_admin_uBsj");
        dataSource.setPassword("a7r6UIwxa34eY0n5");

        FluentConfiguration configure = Flyway.configure();
        configure.dataSource(dataSource);
        Flyway flyway = configure.schemas("onlineshop").load();
        flyway.clean();
        flyway.migrate();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("INSERT INTO onlineshop.users (id, name, password) VALUES(1, 'root', 'GOD');");
            statement.execute("INSERT INTO onlineshop.users (id, name, password) VALUES(2, 'game', 'ORIGIN');");
            statement.execute("INSERT INTO onlineshop.users (id, name, password) VALUES(3, 'play', 'SUCK');");
        }

        securityService = new SecurityService(dataSource);
    }

    @DisplayName("Using correct login and password - get authenticated")
    @Test
    void test_loginExistingUserCorrectPassword_Success() throws SQLException {
        assertTrue(securityService.isLoginPassValid("root", "GOD"));
    }

    @DisplayName("Using correct login and incorrect password - get rejected")
    @Test
    void test_loginExistingUserInvalidPassword_Reject() throws SQLException {
        assertFalse(securityService.isLoginPassValid("root", "DEVIL"));
    }

    @DisplayName("Using nonexistent login - get rejected")
    @Test
    void test_loginNonexistentUser_Reject() throws SQLException {
        assertFalse(securityService.isLoginPassValid("joe", "noone"));
    }

    @DisplayName("Using existing login - create new token - and get its value")
    @Test
    void test_createToken_getValue() {
        String token = securityService.createToken("root", "11");
        assertNotNull(token);
        assertEquals(token.length(), 36);
    }

    @DisplayName("Using nonexistent login - create new token - and get error")
    @Test
    void test_nonexistentUser_createToken_getError() {
        assertThrows(RuntimeException.class, () -> securityService.createToken("user", "11"));
    }

    @DisplayName("Check existing token - receive true")
    @Test
    void test_checkValidToken_getTrue() {
        String token = securityService.createToken("root", "11");
        assertTrue(securityService.isTokenValid(token));
    }

    @DisplayName("Check nonexistent token - receive false")
    @Test
    void test_checkInvalidToken_getFalse() {
        assertFalse(securityService.isTokenValid(UUID.randomUUID().toString()));
    }
}