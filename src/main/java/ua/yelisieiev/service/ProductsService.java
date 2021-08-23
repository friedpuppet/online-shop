package ua.yelisieiev.service;

import ua.yelisieiev.entity.Product;
import ua.yelisieiev.persistence.ProductPersistence;
import ua.yelisieiev.persistence.PersistenceException;

import java.util.List;

public class ProductsService {

    public void setProductPersistence(ProductPersistence productPersistence) {
        this.productPersistence = productPersistence;
    }

    private ProductPersistence productPersistence;

    public ProductsService(ProductPersistence productPersistence) {
        this.productPersistence = productPersistence;
    }

    public List<Product> getAll() throws ProductServiceException {
        try {
            return productPersistence.getAll();
        } catch (PersistenceException e) {
            throw new ProductServiceException(e);
        }
    }

    /**
     * takes @Product instance with @Product.id == null
     * creates corresponding object in storage
     * and fills its @Product.id with a newly generated one
     */
    public void add(Product product) throws ProductServiceException {
        try {
            productPersistence.add(product);
        } catch (PersistenceException e) {
            throw new ProductServiceException(e);
        }
    }

    public Product get(Product.Id productId) throws ProductServiceException {
        try {
            return productPersistence.get(productId);
        } catch (PersistenceException e) {
            throw new ProductServiceException(e);
        }
    }

    public void delete(Product.Id id) throws ProductServiceException {
        try {
            productPersistence.delete(id);
        } catch (PersistenceException e) {
            throw new ProductServiceException(e);
        }
    }

    public void update(Product updatedProduct) throws ProductServiceException {
        try {
            productPersistence.update(updatedProduct);
        } catch (PersistenceException e) {
            throw new ProductServiceException(e);
        }
    }

    public List<Product> getAllFiltered(String searchExpression) throws ProductServiceException {
        try {
            return productPersistence.getAllFiltered(searchExpression);
        } catch (PersistenceException e) {
            throw new ProductServiceException(e);
        }
    }
}
