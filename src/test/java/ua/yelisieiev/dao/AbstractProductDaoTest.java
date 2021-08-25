package ua.yelisieiev.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.yelisieiev.entity.Product;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
abstract public class AbstractProductDaoTest {

    ProductDao productDao;

    public void setDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    @DisplayName("On a live persistence - add a product - see that it is returned")
    @Test
    void test_addProduct_seeThatItIsReturned() throws DaoException {
        Product eggs = new Product("Eggs", 28, "Nasha ryaba");
        productDao.add(eggs);
        assertEquals(eggs, productDao.get(eggs.getId()));
    }

    @DisplayName("On a live persistence - delete an existing product  - see that it isn't returned")
    @Test
    void test_deleteProduct_seeThatItsNoMoreThere() throws DaoException {
        Product eggs = new Product("Eggs", 28, "Nasha ryaba");
        productDao.add(eggs);
        productDao.delete(eggs.getId());
        assertNull(productDao.get(eggs.getId()));
    }

    @DisplayName("On a live persistence - delete an nonexistent product  - see that no exception is thrown")
    @Test
    void test_deleteNonexistentProduct_seeThatItsOk() throws DaoException {
        Product eggs = new Product(new Product.Id(1), "Eggs", 28, "Nasha ryaba");
        productDao.delete(eggs.getId());
    }

    @DisplayName("On a live persistence - update an existing product  - see that it was changed")
    @Test
    void test_updateProduct_seeThatItsChanged() throws DaoException {
        Product eggs = new Product("Eggs", 28);
        productDao.add(eggs);
        Product biggerEggs = new Product(eggs.getId(), "BiggerEggs", 38, "Nash ostrich");
        productDao.update(biggerEggs);
        assertEquals(biggerEggs, productDao.get(eggs.getId()));
    }

    @DisplayName("On a live persistence - update an nonexistent product  - see that it was changed")
    @Test
    void test_updateNonexistentProduct_throwException() {
        Product eggs = new Product(new Product.Id(1), "Eggs", 28, "Nasha ryaba");
        assertThrows(DaoException.class, () -> productDao.update(eggs));
    }

    @Mock
    private ProductDao brokenProductDao;

    @DisplayName("On a broken persistence - try to get a product - throws exception")
    @Test
    void test_onBrokenPersistence_getProduct_throwsException() throws DaoException {
        // todo: it's no test, need to research more on the topic
        when(brokenProductDao.get(any())).thenThrow(DaoException.class);
        Product eggs = new Product(new Product.Id(1), "Eggs", 28, "Nasha ryaba");
        brokenProductDao.add(eggs);
        assertThrows(DaoException.class, () -> brokenProductDao.get(new Product.Id(1)));
    }

    @DisplayName("On a live persistence - create two products - get them")
    @Test
    void test_getAll_getTwo() throws DaoException {
        productDao.add(new Product("Eggs", 34, "Nasha ryaba"));
        productDao.add(new Product("Milk", 31, "Prostokvashino"));
        List<Product> products = productDao.getAll();
        assertEquals(2, products.size());
    }
}