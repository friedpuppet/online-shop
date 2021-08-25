//package ua.yelisieiev.service;
//
//import org.flywaydb.core.Flyway;
//import org.flywaydb.core.api.configuration.FluentConfiguration;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.postgresql.ds.PGSimpleDataSource;
//import ua.yelisieiev.entity.Product;
//import ua.yelisieiev.dao.DaoException;
//import ua.yelisieiev.dao.ProductDao;
//import ua.yelisieiev.dao.jdbc.JdbcProductDao;
//
//import java.sql.SQLException;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class ProductsServiceITest {
//
//    private ProductsService productsService;
//
//    @BeforeEach
//    private void createService() throws DaoException, SQLException {
//        PGSimpleDataSource dataSource = new PGSimpleDataSource();
//        String[] bdServers = {"instances.spawn.cc"};
//        dataSource.setServerNames(bdServers);
//        int[] bdPorts = {32213};
//        dataSource.setPortNumbers(bdPorts);
//        dataSource.setDatabaseName("foobardb");
//        dataSource.setUser("spawn_admin_uBsj");
//        dataSource.setPassword("a7r6UIwxa34eY0n5");
//
//        FluentConfiguration configure = Flyway.configure();
//        configure.dataSource(dataSource);
//        configure.locations("/db/migration");
//        Flyway flyway = configure.schemas("onlineshop").load();
//        flyway.clean();
//        flyway.migrate();
//
//        ProductDao productDao = new JdbcProductDao(dataSource);
//        productsService = new ProductsService(productDao);
//    }
//
//    @DisplayName("With empty persistence - add a product - and check if the service returns it")
//    @Test
//    void test_addProduct_checkAdded() throws ProductServiceException {
//        Product milk = new Product("Milk", 35, "Prostokvashino");
//        productsService.add(milk);
//        assertEquals(milk, productsService.get(milk.getId()));
//    }
//
//    @DisplayName("With populated persistence - delete a product - and check if it was deleted")
//    @Test
//    void test_deleteProduct_check() throws ProductServiceException {
//        Product milk = new Product("Milk", 35, "Prostokvashino");
//        productsService.add(milk);
//        productsService.delete(milk.getId());
//        assertNull(productsService.get(milk.getId()));
//    }
//
//    @DisplayName("With populated persistence - update a product - and check if it was updated")
//    @Test
//    void test_updateProduct_check() throws ProductServiceException {
//        Product milk = new Product("Milk", 35, "Prostokvashino");
//        productsService.add(milk);
//        Product newMilk = new Product(milk.getId(), "PriceyMilk", 37, "Neprostokvashino");
//        productsService.update(newMilk);
//        assertEquals(newMilk, productsService.get(milk.getId()));
//    }
//
//    @DisplayName("With a persistence - perform an update for nonexistent product - get an exception")
//    @Test
//    void test_addInvalidProduct_throwException() {
//        Product product = new Product(new Product.Id(1), "Milk", 35, "Prostokvashino");
//
//        assertThrows(ProductServiceException.class, () -> productsService.update(product));
//    }
//
//}
