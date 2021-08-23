package ua.yelisieiev.service.mock;

import ua.yelisieiev.entity.Product;
import ua.yelisieiev.persistence.PersistenceException;
import ua.yelisieiev.persistence.ProductPersistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductPersistenceMock implements ProductPersistence {
    Map<Product.Id, Product> products = new HashMap<>();

    @Override
    public List<Product> getAll() throws PersistenceException {
        return products.values().stream().toList();
    }

    @Override
    public List<Product> getAllFiltered(String searchExpression) throws PersistenceException {
        return null;
    }

    @Override
    public void add(Product product) throws PersistenceException {
        products.put(product.getId(), product);
    }

    @Override
    public Product get(Product.Id productId) throws PersistenceException {
        return products.get(productId);
    }

    @Override
    public void delete(Product.Id productId) throws PersistenceException {
        products.remove(productId);
    }

    @Override
    public void update(Product updatedProduct) throws PersistenceException {
        if (products.get(updatedProduct.getId()) == null) {
            throw new PersistenceException("No such product");
        }
        products.replace(updatedProduct.getId(), updatedProduct);
    }
}
