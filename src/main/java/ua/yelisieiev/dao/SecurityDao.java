package ua.yelisieiev.dao;

import ua.yelisieiev.entity.AuthTokenWithTTL;
import ua.yelisieiev.entity.User;

import java.util.Optional;

public interface SecurityDao {
    Optional<AuthTokenWithTTL> getTokenByString(String tokenString);

    void saveUserToken(String login, AuthTokenWithTTL token);

    void deleteToken(String tokenString);

    void createUser(User user);

    Optional<User> getUser(String login);
}
