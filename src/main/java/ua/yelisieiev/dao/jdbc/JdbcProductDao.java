package ua.yelisieiev.dao.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ua.yelisieiev.entity.Product;
import ua.yelisieiev.dao.ProductDao;
import ua.yelisieiev.dao.DaoException;

import java.sql.*;
import java.util.List;

public class JdbcProductDao implements ProductDao {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private static final RowMapper<Product> PRODUCT_ROW_MAPPER =
            (ResultSet resultSet, int rowNum) -> getProductFromResultSetRow(resultSet);

    private final JdbcTemplate jdbcTemplate;

    public JdbcProductDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Product> getAll() throws DaoException {
        return jdbcTemplate.query("select id, name, price, description, modify_date " +
                        "from onlineshop.products",
                PRODUCT_ROW_MAPPER);
    }

    @Override
    public List<Product> getAllFiltered(String searchExpression) throws DaoException {
        String likeExpr = "%" + searchExpression + "%";
        return jdbcTemplate.query("select id, name, price, description, modify_date " +
                        "from onlineshop.products prods " +
                        "WHERE prods.name like ? or prods.description like ?",
                PRODUCT_ROW_MAPPER,
                likeExpr, likeExpr);
    }

    @Override
    public void add(Product product) throws DaoException {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(ps -> {
                    PreparedStatement preparedStatement = ps.prepareStatement(
                            "insert into onlineshop.products(name, price, description, modify_date) " +
                                    "values (?, ?, ?, current_timestamp)", Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setString(1, product.getName());
                    preparedStatement.setDouble(2, product.getPrice());
                    preparedStatement.setString(3, product.getDescription());
                    return preparedStatement;
                },
                keyHolder);
//        LOGGER.info("Key list size {}", keyHolder.getKeyList().size());
        product.setId(new Product.Id((Integer) keyHolder.getKeys().get("id")));
    }

    @Override
    public Product get(Product.Id productId) throws DaoException {
        try {
            return jdbcTemplate.queryForObject("select id, name, price, description, modify_date " +
                            "from onlineshop.products where id = ?",
                    PRODUCT_ROW_MAPPER,
                    productId.getValue());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void delete(Product.Id productId) throws DaoException {
        jdbcTemplate.update("delete from onlineshop.products where id = ?", productId.getValue());
    }

    @Override
    public void update(Product updatedProduct) throws DaoException {
        final int updatedCount = jdbcTemplate.update("update onlineshop.products " +
                        "set name = ?, price = ?, description = ?, modify_date = current_timestamp " +
                        "where id = ?",
                updatedProduct.getName(), updatedProduct.getPrice(), updatedProduct.getDescription(),
                updatedProduct.getId().getValue());
        if (updatedCount == 0) {
            throw new DaoException("No product to update");
        }
    }

    private static Product getProductFromResultSetRow(ResultSet resultSet) throws SQLException {
        return new Product(new Product.Id(resultSet.getInt("id")),
                resultSet.getString("name"),
                resultSet.getDouble("price"),
                resultSet.getString("description"),
                resultSet.getTimestamp("modify_date"));
    }
}
