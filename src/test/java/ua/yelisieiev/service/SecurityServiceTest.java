package ua.yelisieiev.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.yelisieiev.dao.SecurityDao;
import ua.yelisieiev.entity.Role;
import ua.yelisieiev.entity.TokenWithTTL;
import ua.yelisieiev.service.mock.MockSecurityDao;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SecurityServiceTest {

    private SecurityService securityService;

    @BeforeEach
    void setUp() throws SQLException {
        SecurityDao dao = new MockSecurityDao();

        securityService = new SecurityService(dao);
        securityService.createUser("root", "GOD", Role.ADMIN);
        securityService.createUser("alex", "DEVIL");
        TokenWithTTL rootToken = new TokenWithTTL("0d0d611a-1994-46c2-a763-d47ca5df6f38", LocalDateTime.now().plusHours(4));
        securityService.createAndSaveToken("root");
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
        TokenWithTTL token = securityService.createAndSaveToken("root");
        assertNotNull(token);
        assertEquals(token.getToken().length(), 36);
    }

    @DisplayName("Using nonexistent login - create new token - and get error")
    @Test
    void test_nonexistentUser_createToken_getError() {
        assertThrows(RuntimeException.class, () -> securityService.createAndSaveToken("user"));
    }

    @DisplayName("Check existing token - receive true")
    @Test
    void test_checkValidToken_getTrue() {
        TokenWithTTL token = securityService.createAndSaveToken("root");
        assertTrue(securityService.isTokenValid(token.getToken()));
    }

    @DisplayName("Check nonexistent token - receive false")
    @Test
    void test_checkInvalidToken_getFalse() {
        assertFalse(securityService.isTokenValid(UUID.randomUUID().toString()));
    }

    @DisplayName("With existing token - logout")
    @Test
    void test_existingTokenLogout() {
        Optional<TokenWithTTL> token = securityService.login("root", "GOD");
        securityService.logout(token.get().getToken());
        assertFalse(securityService.isTokenValid(token.get().getToken()));
    }

    @DisplayName("With nonexistent token - logout")
    @Test
    void test_nonexistentTokenLogout() {
        securityService.logout("000000000");
        assertFalse(securityService.isTokenValid("000000000"));
    }

    @DisplayName("With existing token - get role ADMIN")
    @Test
    void test_existingToken_getRoleAdmin() {
        Optional<TokenWithTTL> token = securityService.login("root", "GOD");
        Role role = securityService.getTokenRole(token.get().getToken());
        assertEquals(Role.ADMIN, role);
    }

    @DisplayName("With nonexistent token - get role GUEST")
    @Test
    void test_nonexistentToken_getRoleGuest() {
        Role role = securityService.getTokenRole("123");
        assertEquals(Role.GUEST, role);
    }
}