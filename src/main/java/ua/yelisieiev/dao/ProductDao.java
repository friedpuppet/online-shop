package ua.yelisieiev.dao;

import ua.yelisieiev.entity.Product;

import java.util.List;

public interface ProductDao {
    List<Product> getAll() throws DaoException;

    List<Product> getAllFiltered(String searchExpression) throws DaoException;

    void add(Product product) throws DaoException;

    Product get(Product.Id productId) throws DaoException;

    void delete(Product.Id productId) throws DaoException;

    void update(Product updatedProduct) throws DaoException;
}
