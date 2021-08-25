package ua.yelisieiev.dao.jdbc;

import ua.yelisieiev.dao.DaoException;
import ua.yelisieiev.dao.SecurityDao;
import ua.yelisieiev.entity.AuthTokenWithTTL;
import ua.yelisieiev.entity.User;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class JdbcSecurityDao implements SecurityDao {

    DataSource dataSource;

    public JdbcSecurityDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<AuthTokenWithTTL> getTokenByString(String tokenString) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.
                     prepareStatement("SELECT t.valid_until FROM onlineshop.tokens t " +
                             "WHERE t.token = ? and t.valid_until >= CURRENT_TIMESTAMP;")) {
            statement.setString(1, tokenString);
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                final LocalDateTime valid_until = resultSet.getTimestamp("valid_until").toLocalDateTime();
                AuthTokenWithTTL authTokenWithTTL = new AuthTokenWithTTL(tokenString, valid_until);
                return Optional.of(authTokenWithTTL);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving token", e);
        }
    }

    @Override
    public void saveUserToken(String login, AuthTokenWithTTL token) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.
                     prepareStatement("INSERT INTO onlineshop.tokens (token, user_id, valid_until) \n" +
                             "VALUES (?, (select u.id from onlineshop.users u where u.name = ?), ?);")) {
            statement.setString(1, token.getToken());
            statement.setString(2, login);
            statement.setTimestamp(3, Timestamp.valueOf(token.getValidUntil()));
            statement.execute();
            if (statement.getUpdateCount() != 1) {
                throw new DaoException("Exactly one row should've been inserted, but the number is " +
                        statement.getUpdateCount());
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Error retrieving token", e);
        }
    }

    @Override
    public void deleteToken(String tokenString) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.
                     prepareStatement("DELETE FROM onlineshop.tokens WHERE token = ?;")) {
            statement.setString(1, tokenString);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting token", e);
        }
    }

    @Override
    public void createUser(User user) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.
                     prepareStatement("INSERT INTO onlineshop.users(" +
                             "name, password_hash, salt) " +
                             "VALUES (?, ?, ?);")) {
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getPasswordSalt());
            statement.execute();
            if (statement.getUpdateCount() != 1) {
                throw new DaoException("Exactly one row should've been inserted, but the number is " +
                        statement.getUpdateCount());
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Error retrieving token", e);
        }
    }

    @Override
    public Optional<User> getUser(String login) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.
                     prepareStatement("select u.password_hash, u.salt from onlineshop.users u where u.name = ?")) {
            statement.setString(1, login);
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                User user = new User(login);
                user.setPasswordHash(resultSet.getString("password_hash"));
                user.setPasswordSalt(resultSet.getString("salt"));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving token", e);
        }
    }

}
