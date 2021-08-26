package ua.yelisieiev.dao.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ua.yelisieiev.dao.SecurityDao;
import ua.yelisieiev.entity.Role;
import ua.yelisieiev.entity.TokenWithTTL;
import ua.yelisieiev.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

public class JdbcSecurityDao implements SecurityDao {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private static final RowMapper<User> USER_ROW_MAPPER =
            (ResultSet resultSet, int rowNum) -> getUserFromResultSetRow(resultSet);
    private static final RowMapper<TokenWithTTL> TOKEN_ROW_MAPPER =
            (ResultSet resultSet, int rowNum) -> getTokenFromResultSetRow(resultSet);
    private static final RowMapper<Role> ROLE_ROW_MAPPER =
            (ResultSet resultSet, int rowNum) -> getRoleFromResultSetRow(resultSet);

    private final JdbcTemplate jdbcTemplate;

    public JdbcSecurityDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<TokenWithTTL> getTokenByString(String tokenString) {
        try {
            TokenWithTTL token = jdbcTemplate.queryForObject("SELECT t.token, t.valid_until FROM onlineshop.tokens t " +
                    "WHERE t.token = ? and t.valid_until >= CURRENT_TIMESTAMP;", TOKEN_ROW_MAPPER, tokenString);
            return Optional.ofNullable(token);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void saveUserToken(String login, TokenWithTTL token) {
        jdbcTemplate.update("INSERT INTO onlineshop.tokens (token, user_id, valid_until) " +
                        "VALUES (?, (select u.id from onlineshop.users u where u.name = ?), ?);",
                token.getToken(), login, Timestamp.valueOf(token.getValidUntil()));
    }

    @Override
    public void deleteToken(String tokenString) {
        jdbcTemplate.update("DELETE FROM onlineshop.tokens WHERE token = ?;",
                tokenString);
    }

    @Override
    public Optional<Role> getTokenRole(String tokenString) {
        try {
            Role role = jdbcTemplate.queryForObject("SELECT u.role_name FROM onlineshop.tokens t " +
                    "JOIN onlineshop.users u ON t.user_id = u.id " +
                    "WHERE t.token = ?", ROLE_ROW_MAPPER, tokenString);
            return Optional.ofNullable(role);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void createUser(User user) {
        jdbcTemplate.update("INSERT INTO onlineshop.users(" +
                        "name, password_hash, salt, role_name) " +
                        "VALUES (?, ?, ?, ?);",
                user.getLogin(), user.getPasswordHash(), user.getPasswordSalt(), user.getRole().toString());
    }

    @Override
    public Optional<User> getUser(String login) {
        try {
            User user = jdbcTemplate.queryForObject("select u.name, u.password_hash, u.salt, u.role_name " +
                    "from onlineshop.users u where u.name = ?", USER_ROW_MAPPER, login);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private static Role getRoleFromResultSetRow(ResultSet resultSet) throws SQLException {
        return Role.of(resultSet.getString("role_name"));
    }

    private static User getUserFromResultSetRow(ResultSet resultSet) throws SQLException {
        User user = new User(resultSet.getString("name"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setPasswordSalt(resultSet.getString("salt"));
        user.setRole(Role.of(resultSet.getString("role_name")));
        return user;
    }

    private static TokenWithTTL getTokenFromResultSetRow(ResultSet resultSet) throws SQLException {
        final String tokenString = resultSet.getString("token");
        LocalDateTime valid_until = resultSet.getTimestamp("valid_until").toLocalDateTime();
        return new TokenWithTTL(tokenString, valid_until);
    }
}
