package ua.yelisieiev.service.mock;

import ua.yelisieiev.dao.SecurityDao;
import ua.yelisieiev.entity.Role;
import ua.yelisieiev.entity.TokenWithTTL;
import ua.yelisieiev.entity.User;

import java.util.*;

public class MockSecurityDao implements SecurityDao {
    private final List<User> usersDB = new ArrayList<>(2);
    private final Map<String, TokenWithTTL> usersTokensMap = new HashMap<>(2);

    @Override
    public Optional<TokenWithTTL> getTokenByString(String tokenString) {
        for (TokenWithTTL token : usersTokensMap.values()) {
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
    public void saveUserToken(String login, TokenWithTTL token) {
        if (!usersTokensMap.containsKey(login)) {
            throw new RuntimeException("No user");
        }
        usersTokensMap.put(login, token);
    }

    @Override
    public void deleteToken(String tokenString) {
        for (Map.Entry<String, TokenWithTTL> entry : usersTokensMap.entrySet()) {
            TokenWithTTL value = entry.getValue();
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

    @Override
    public Optional<Role> getTokenRole(String tokenString) {
        Optional<User> user = getUserByToken(tokenString);
        if (user.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(user.get().getRole());
    }

    private Optional<User> getUserByToken(String tokenString) {
        for (Map.Entry<String, TokenWithTTL> entry : usersTokensMap.entrySet()) {
            TokenWithTTL value = entry.getValue();
            if (value != null && value.getToken().equals(tokenString)) {
                return getUser(entry.getKey());
            }
        }
        return Optional.empty();
    }
}
