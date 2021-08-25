package ua.yelisieiev.service;

import org.springframework.beans.factory.annotation.Autowired;
import ua.yelisieiev.dao.SecurityDao;
import ua.yelisieiev.entity.AuthTokenWithTTL;
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

    public Optional<AuthTokenWithTTL> login(String login, String providedPassword) {
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

    protected AuthTokenWithTTL createAndSaveToken(String login) {
        String tokenString = UUID.randomUUID().toString();
        AuthTokenWithTTL token = new AuthTokenWithTTL(tokenString, LocalDateTime.now().plusMinutes(TOKEN_TTL_MINUTES));
        dao.saveUserToken(login, token);
        return token;
    }

    public boolean isTokenValid(String tokenString) {
        if (tokenString == null) {
            return false;
        }
        Optional<AuthTokenWithTTL> token = dao.getTokenByString(tokenString);
        if (token.isEmpty()) {
            return false;
        }
        if (token.get().getValidUntil().isBefore(LocalDateTime.now())) {
            return false;
        }

        return true;
    }

    public void createUser(String login, String password) {
        User user = new User(login);
        final String passwordSalt = Encrypter.generateSalt();
        user.setPasswordSalt(passwordSalt);
        user.setPasswordHash(Encrypter.encryptPassword(password, passwordSalt));
        dao.createUser(user);
    }

    public void logout(String tokenString) {
        dao.deleteToken(tokenString);
    }
}
