package ua.yelisieiev.service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.UUID;

public class SecurityService {
    private DataSource dataSource;

    public SecurityService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isLoginPassValid(String login, String password) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.
                     prepareStatement("SELECT count(1) AS cnt FROM onlineshop.users u where u.name = ? AND u.password = ?;")) {
            statement.setString(1, login);
            statement.setString(2, password);
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                if (!resultSet.next()) {
                    return false;
                }
                Integer count = resultSet.getInt(1);
                if (count == 0) {
                    return false;
                }
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error validating user", e);
        }
    }

    public String createToken(String login, String sessionId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.
                     prepareStatement("INSERT INTO onlineshop.tokens (user_id, token) " +
                             "SELECT id, ? FROM onlineshop.users u WHERE u.name = ?;")) {
            String token = UUID.randomUUID().toString();
            statement.setString(1, token);
            statement.setString(2, login);
            statement.execute();
            if (statement.getUpdateCount() != 1) {
                throw new SQLException("No token was created");
            }
            return token;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating token", e);
        }
    }

    public boolean isTokenValid(String token) {
        if (token == null) {
            return false;
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.
                     prepareStatement("SELECT 1 FROM onlineshop.tokens t WHERE t.token = ?;")) {
            statement.setString(1, token);
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error validating token", e);
        }
    }
}
