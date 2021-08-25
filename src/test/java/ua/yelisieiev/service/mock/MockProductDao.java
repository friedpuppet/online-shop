package ua.yelisieiev.service.mock;

import ua.yelisieiev.entity.Product;
import ua.yelisieiev.dao.DaoException;
import ua.yelisieiev.dao.ProductDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockProductDao implements ProductDao {
    Map<Product.Id, Product> products = new HashMap<>();

    @Override
    public List<Product> getAll() throws DaoException {
        return products.values().stream().toList();
    }

    @Override
    public List<Product> getAllFiltered(String searchExpression) throws DaoException {
        return null;
    }

    @Override
    public void add(Product product) throws DaoException {
        products.put(product.getId(), product);
    }

    @Override
    public Product get(Product.Id productId) throws DaoException {
        return products.get(productId);
    }

    @Override
    public void delete(Product.Id productId) throws DaoException {
        products.remove(productId);
    }

    @Override
    public void update(Product updatedProduct) throws DaoException {
        if (products.get(updatedProduct.getId()) == null) {
            throw new DaoException("No such product");
        }
        products.replace(updatedProduct.getId(), updatedProduct);
    }
}
