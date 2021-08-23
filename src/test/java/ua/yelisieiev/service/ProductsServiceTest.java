package ua.yelisieiev.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.yelisieiev.entity.Product;
import ua.yelisieiev.persistence.PersistenceException;
import ua.yelisieiev.service.mock.ProductPersistenceMock;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ProductsServiceTest {

    private ProductsService productsService;

    @BeforeEach
    private void createService() throws PersistenceException, SQLException {
        productsService = new ProductsService(new ProductPersistenceMock());
    }

    @DisplayName("With empty persistence - add a product - and check if the service returns it")
    @Test
    void test_addProduct_checkAdded() throws ProductServiceException {
        Product milk = new Product("Milk", 35, "Prostokvashino");
        productsService.add(milk);
        assertEquals(milk, productsService.get(milk.getId()));
    }

    @DisplayName("With populated persistence - delete a product - and check if it was deleted")
    @Test
    void test_deleteProduct_check() throws ProductServiceException {
        Product milk = new Product("Milk", 35, "Prostokvashino");
        productsService.add(milk);
        productsService.delete(milk.getId());
        assertNull(productsService.get(milk.getId()));
    }

    @DisplayName("With populated persistence - update a product - and check if it was updated")
    @Test
    void test_updateProduct_check() throws ProductServiceException {
        Product milk = new Product("Milk", 35, "Prostokvashino");
        productsService.add(milk);
        Product newMilk = new Product(milk.getId(), "PriceyMilk", 37, "Neprostokvashino");
        productsService.update(newMilk);
        assertEquals(newMilk, productsService.get(milk.getId()));
    }

    @DisplayName("With a persistence - perform an update for nonexistent product - get an exception")
    @Test
    void test_addInvalidProduct_throwException() {
        Product product = new Product(new Product.Id(1), "Milk", 35, "Prostokvashino");

        assertThrows(ProductServiceException.class, () -> productsService.update(product));
    }

}
