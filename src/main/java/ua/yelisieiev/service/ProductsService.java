package ua.yelisieiev.service;

import org.springframework.beans.factory.annotation.Autowired;
import ua.yelisieiev.entity.Product;
import ua.yelisieiev.dao.ProductDao;
import ua.yelisieiev.dao.DaoException;

import java.util.List;

public class ProductsService {

//    public void setProductDao(ProductDao productDao) {
//        this.productDao = productDao;
//    }

    private final ProductDao productDao;

    public ProductsService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public List<Product> getAll() throws ProductServiceException {
        try {
            return productDao.getAll();
        } catch (DaoException e) {
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
            productDao.add(product);
        } catch (DaoException e) {
            throw new ProductServiceException(e);
        }
    }

    public Product get(Product.Id productId) throws ProductServiceException {
        try {
            return productDao.get(productId);
        } catch (DaoException e) {
            throw new ProductServiceException(e);
        }
    }

    public void delete(Product.Id id) throws ProductServiceException {
        try {
            productDao.delete(id);
        } catch (DaoException e) {
            throw new ProductServiceException(e);
        }
    }

    public void update(Product updatedProduct) throws ProductServiceException {
        try {
            productDao.update(updatedProduct);
        } catch (DaoException e) {
            throw new ProductServiceException(e);
        }
    }

    public List<Product> getAllFiltered(String searchExpression) throws ProductServiceException {
        try {
            return productDao.getAllFiltered(searchExpression);
        } catch (DaoException e) {
            throw new ProductServiceException(e);
        }
    }
}
