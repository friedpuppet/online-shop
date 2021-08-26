package ua.yelisieiev.dao;

import ua.yelisieiev.entity.Role;
import ua.yelisieiev.entity.TokenWithTTL;
import ua.yelisieiev.entity.User;

import java.util.Optional;

public interface SecurityDao {
    Optional<TokenWithTTL> getTokenByString(String tokenString);

    void saveUserToken(String login, TokenWithTTL token);

    void deleteToken(String tokenString);

    void createUser(User user);

    Optional<User> getUser(String login);

    Optional<Role> getTokenRole(String tokenString);
}
