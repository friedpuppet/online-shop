package ua.yelisieiev.persistence.jdbc;

import ua.yelisieiev.entity.Product;
import ua.yelisieiev.persistence.ProductPersistence;
import ua.yelisieiev.persistence.PersistenceException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class JdbcProductPersistence implements ProductPersistence {
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private DataSource dataSource;

    public JdbcProductPersistence(DataSource dataSource) throws PersistenceException {
        this.dataSource = dataSource;
    }

    @Override
    public List<Product> getAll() throws PersistenceException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement getAllStatement = getGetAllStatement(connection)) {
            getAllStatement.execute();
            try (ResultSet resultSet = getAllStatement.getResultSet()) {
                LinkedList<Product> products = new LinkedList<>();
                while (resultSet.next()) {
                    Product product = getProductFromResultSetRow(resultSet);
                    products.add(product);
                }
                return products;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Unable to get products", e);
        }
    }

    @Override
    public List<Product> getAllFiltered(String searchExpression) throws PersistenceException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement getAllFilteredStatement = getGetAllFilteredStatement(connection)) {
            getAllFilteredStatement.setString(1, "%" + searchExpression + "%");
            getAllFilteredStatement.setString(2, "%" + searchExpression + "%");
            getAllFilteredStatement.execute();
            try (ResultSet resultSet = getAllFilteredStatement.getResultSet()) {
                LinkedList<Product> products = new LinkedList<>();
                while (resultSet.next()) {
                    Product product = getProductFromResultSetRow(resultSet);
                    products.add(product);
                }
                return products;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Unable to get products", e);
        }
    }

    @Override
    synchronized public void add(Product product) throws PersistenceException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement addStatement = getAddStatement(connection)) {
//            addStatement.setInt(1, newId);
            addStatement.setString(1, product.getName());
            addStatement.setDouble(2, product.getPrice());
            addStatement.setString(3, product.getDescription());
            addStatement.executeUpdate();
            try(ResultSet keySet = addStatement.getGeneratedKeys()) {
                keySet.next();
                int newId = keySet.getInt(1);
                product.setId(new Product.Id(newId));
            }
        } catch (SQLException e) {
            throw new PersistenceException("Unable to add product " + product, e);
        }
    }

    @Override
    public Product get(Product.Id productId) throws PersistenceException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement getStatement = getGetStatement(connection)) {
            getStatement.setInt(1, productId.getValue());
            getStatement.execute();
            try (ResultSet resultSet = getStatement.getResultSet()) {
                if (!resultSet.next()) {
                    return null;
                }
                return getProductFromResultSetRow(resultSet);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Unable to get product with id {" + productId + "}", e);
        }
    }

    @Override
    public void delete(Product.Id productId) throws PersistenceException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement getStatement = getDeleteStatement(connection)) {
            getStatement.setInt(1, productId.getValue());
            getStatement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Unable to delete product with id {" + productId + "}", e);
        }
    }

    @Override
    public void update(Product updatedProduct) throws PersistenceException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement updateStatement = getUpdateStatement(connection)) {
            updateStatement.setString(1, updatedProduct.getName());
            updateStatement.setDouble(2, updatedProduct.getPrice());
            updateStatement.setString(3, updatedProduct.getDescription());
            updateStatement.setInt(4, updatedProduct.getId().getValue());
            updateStatement.executeUpdate();
            if (updateStatement.getUpdateCount() == 0) {
                throw new PersistenceException("No product to update");
            }
        } catch (SQLException e) {
            throw new PersistenceException("Unable to update product " + updatedProduct, e);
        }
    }

    private PreparedStatement getAddStatement(Connection connection) throws SQLException {
        String ADD_SQL = "insert into onlineshop.products(name, price, description, modify_date) values (?, ?, ?, current_timestamp)";
        return connection.prepareStatement(ADD_SQL, Statement.RETURN_GENERATED_KEYS);
    }

    private PreparedStatement getGetAllStatement(Connection connection) throws SQLException {
        // being straightforward here; no reflection-autogeneration
        String GETALL_SQL = "select id, name, price, description, modify_date from onlineshop.products";
        return connection.prepareStatement(GETALL_SQL);
    }

    private PreparedStatement getGetAllFilteredStatement(Connection connection) throws SQLException {
        String GETALL_SQL = "select id, name, price, description, modify_date from onlineshop.products prods" +
                " WHERE prods.name like ? or prods.description like ?";
        return connection.prepareStatement(GETALL_SQL);
    }

    private PreparedStatement getGetStatement(Connection connection) throws SQLException {
        String GET_SQL = "select id, name, price, description, modify_date from onlineshop.products where id = ?";
        return connection.prepareStatement(GET_SQL);
    }

    private PreparedStatement getDeleteStatement(Connection connection) throws SQLException {
        String DELETE_SQL = "delete from onlineshop.products where id = ?";
        return connection.prepareStatement(DELETE_SQL);
    }

    private PreparedStatement getUpdateStatement(Connection connection) throws SQLException {
        String UPDATE_SQL = "update onlineshop.products set name = ?, price = ?, description = ?, modify_date = current_timestamp where id = ?";
        return connection.prepareStatement(UPDATE_SQL);
    }

    private Product getProductFromResultSetRow(ResultSet resultSet) throws SQLException {
        return new Product(new Product.Id(resultSet.getInt("id")),
                resultSet.getString("name"),
                resultSet.getDouble("price"),
                resultSet.getString("description"),
                resultSet.getTimestamp("modify_date"));
    }
}
