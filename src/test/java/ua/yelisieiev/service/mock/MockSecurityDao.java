package ua.yelisieiev.service.mock;

import ua.yelisieiev.dao.SecurityDao;
import ua.yelisieiev.entity.AuthTokenWithTTL;
import ua.yelisieiev.entity.User;

import java.util.*;

public class MockSecurityDao implements SecurityDao {
    private final List<User> usersDB = new ArrayList<>(2);
    private final Map<String, AuthTokenWithTTL> usersTokensMap = new HashMap<>(2);

    @Override
    public Optional<AuthTokenWithTTL> getTokenByString(String tokenString) {
        for (AuthTokenWithTTL token : usersTokensMap.values()) {
            if (token == null) {
                continue;
            }
            if (token.getToken().equals(tokenString)) {
                return Optional.of(token);
            }
        }
        return Optional.empty();
    }

    @Override
    public void saveUserToken(String login, AuthTokenWithTTL token) {
        if (!usersTokensMap.containsKey(login)) {
            throw new RuntimeException("No user");
        }
        usersTokensMap.put(login, token);
    }

    @Override
    public void deleteToken(String tokenString) {
        for (Map.Entry<String, AuthTokenWithTTL> entry : usersTokensMap.entrySet()) {
            AuthTokenWithTTL value = entry.getValue();
            if (value == null) {
                continue;
            }
            if (value.getToken().equals(tokenString)) {
                usersTokensMap.remove(entry.getKey());
                return;
            }
        }
    }

    @Override
    public void createUser(User user) {
        usersDB.add(user);
        usersTokensMap.put(user.getLogin(), null);
    }

    @Override
    public Optional<User> getUser(String login) {
        for (User user : usersDB) {
            if (user.getLogin().equals(login)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
