package ua.yelisieiev.service;

import org.springframework.beans.factory.annotation.Autowired;
import ua.yelisieiev.dao.SecurityDao;
import ua.yelisieiev.entity.Role;
import ua.yelisieiev.entity.TokenWithTTL;
import ua.yelisieiev.entity.User;
import ua.yelisieiev.util.crypto.Encrypter;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class SecurityService {
    public static final int TOKEN_TTL_MINUTES = 24 * 60;

    @Autowired
    private SecurityDao dao;

    public SecurityService(SecurityDao dao) {
        this.dao = dao;
    }

    public void setDao(SecurityDao dao) {
        this.dao = dao;
    }

    public Optional<TokenWithTTL> login(String login, String providedPassword) {
        if (!isLoginPassValid(login, providedPassword)) {
            return Optional.empty();
        }
        return Optional.of(createAndSaveToken(login));
    }

    protected boolean isLoginPassValid(String login, String providedPassword) {
        Optional<User> user = dao.getUser(login);
        if (user.isEmpty()) {
            return false;
        }
        String providedHash = Encrypter.encryptPassword(providedPassword, user.get().getPasswordSalt());
        if (!providedHash.equals(user.get().getPasswordHash())) {
            return false;
        }
        return true;
    }

    protected TokenWithTTL createAndSaveToken(String login) {
        String tokenString = UUID.randomUUID().toString();
        TokenWithTTL token = new TokenWithTTL(tokenString, LocalDateTime.now().plusMinutes(TOKEN_TTL_MINUTES));
        dao.saveUserToken(login, token);
        return token;
    }

    public boolean isTokenValid(String tokenString) {
        return isTokenValid(tokenString, Role.GUEST);
    }

    public boolean isTokenValid(String tokenString, Role role) {
        if (tokenString == null) {
            return false;
        }
        Optional<TokenWithTTL> token = dao.getTokenByString(tokenString);
        if (token.isEmpty()) {
            return false;
        }
        if (token.get().getValidUntil().isBefore(LocalDateTime.now())) {
            return false;
        }
        Role tokenRole = getTokenRole(tokenString);
        return tokenRole == role || role == Role.GUEST;
    }

    public Role getTokenRole(String tokenString) {
        Optional<Role> roleOptional = dao.getTokenRole(tokenString);
        if (roleOptional.isEmpty()) {
            return Role.GUEST;
        }
        return roleOptional.get();
    }

    public void createUser(String login, String password) {
        createUser(login, password, Role.GUEST);
    }

    public void createUser(String login, String password, Role role) {
        User user = new User(login);
        final String passwordSalt = Encrypter.generateSalt();
        user.setPasswordSalt(passwordSalt);
        user.setPasswordHash(Encrypter.encryptPassword(password, passwordSalt));
        user.setRole(role);
        dao.createUser(user);
    }

    public void logout(String tokenString) {
        dao.deleteToken(tokenString);
    }
}
