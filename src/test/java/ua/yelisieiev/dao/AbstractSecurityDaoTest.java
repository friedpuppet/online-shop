package ua.yelisieiev.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.yelisieiev.entity.Role;
import ua.yelisieiev.entity.TokenWithTTL;
import ua.yelisieiev.entity.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractSecurityDaoTest {
    private SecurityDao securityDao;

    public void setDao(SecurityDao securityDao) {
        this.securityDao = securityDao;
    }

    @DisplayName("For precreated user - save token and get it")
    @Test
    void getTokenByString() {
        User user = new User("root");
        user.setPasswordSalt("0d0d611a-1994-46c2-a763-d47ca5df6f38");
        user.setPasswordHash("3c65c03eff4cd7bbf574ee64d19c28c73fcc238f");
        securityDao.createUser(user);

        TokenWithTTL token = new TokenWithTTL("25b72a92-9123-4f27-8331-5aaa1b069863", LocalDateTime.now().plusHours(4));
        securityDao.saveUserToken("root", token);
        Optional<TokenWithTTL> tokenByString = securityDao.getTokenByString("25b72a92-9123-4f27-8331-5aaa1b069863");
        assertFalse(tokenByString.isEmpty());
        assertEquals("25b72a92-9123-4f27-8331-5aaa1b069863", tokenByString.get().getToken());
    }

    @DisplayName("For authorized ADMIN user - get role")
    @Test
    void getTokenRole() {
        User user = new User("root");
        user.setPasswordSalt("0d0d611a-1994-46c2-a763-d47ca5df6f38");
        user.setPasswordHash("3c65c03eff4cd7bbf574ee64d19c28c73fcc238f");
        user.setRole(Role.ADMIN);
        securityDao.createUser(user);

        TokenWithTTL token = new TokenWithTTL("25b72a92-9123-4f27-8331-5aaa1b069863", LocalDateTime.now().plusHours(4));
        securityDao.saveUserToken("root", token);
        Optional<Role> roleOptional = securityDao.getTokenRole("25b72a92-9123-4f27-8331-5aaa1b069863");
        assertFalse(roleOptional.isEmpty());
        assertEquals(Role.ADMIN, roleOptional.get());
    }

    @DisplayName("For precreated user - retrieve user and check its attributes")
    @Test
    void getUser() {
        User user = new User("root");
        user.setPasswordSalt("0d0d611a-1994-46c2-a763-d47ca5df6f38");
        user.setPasswordHash("3c65c03eff4cd7bbf574ee64d19c28c73fcc238f");
        securityDao.createUser(user);

        final Optional<User> userOpt = securityDao.getUser("root");
        assertFalse(userOpt.isEmpty());
        User retrievedUser = userOpt.get();
        assertEquals("root", retrievedUser.getLogin());
        assertEquals("0d0d611a-1994-46c2-a763-d47ca5df6f38", retrievedUser.getPasswordSalt());
        assertEquals("3c65c03eff4cd7bbf574ee64d19c28c73fcc238f", retrievedUser.getPasswordHash());
    }

    @DisplayName("For existing token - delete it")
    @Test
    void logout() {
        User user = new User("root");
        user.setPasswordSalt("0d0d611a-1994-46c2-a763-d47ca5df6f38");
        user.setPasswordHash("3c65c03eff4cd7bbf574ee64d19c28c73fcc238f");
        securityDao.createUser(user);
        TokenWithTTL token = new TokenWithTTL("25b72a92-9123-4f27-8331-5aaa1b069863", LocalDateTime.now().plusHours(4));
        securityDao.saveUserToken("root", token);

        securityDao.deleteToken("25b72a92-9123-4f27-8331-5aaa1b069863");

        final Optional<TokenWithTTL> storedToken = securityDao.getTokenByString("25b72a92-9123-4f27-8331-5aaa1b069863");
        assertTrue(storedToken.isEmpty());
    }
}
