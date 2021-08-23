package ua.yelisieiev.service;

import org.postgresql.ds.PGSimpleDataSource;
import ua.yelisieiev.persistence.PersistenceException;
import ua.yelisieiev.persistence.ProductPersistence;
import ua.yelisieiev.persistence.jdbc.JdbcProductPersistence;

import java.util.HashMap;
import java.util.Map;

public class ServiceLocator {
    private static final Map<Class<?>, Object> SERVICES = new HashMap<>();

    static {
        // todo read this from a file
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        String[] bdServers = {"instances.spawn.cc"};
        dataSource.setServerNames(bdServers);
        int[] bdPorts = {32090};
        dataSource.setPortNumbers(bdPorts);
        dataSource.setDatabaseName("foobardb");
        dataSource.setUser("spawn_admin_uBsj");
        dataSource.setPassword("4AXKnwjUxnEzFuMa");

        ProductPersistence productPersistence = null;
        try {
            productPersistence = new JdbcProductPersistence(dataSource);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
        ProductsService productsService = new ProductsService(productPersistence);
        addService(ProductsService.class, productsService);

        SecurityService securityService = new SecurityService(dataSource);
        addService(SecurityService.class, securityService);
    }

    public static <T> T getService(Class<T> serviceType) {
        return serviceType.cast(SERVICES.get(serviceType));
    }

    public static void addService(Class<?> serviceName, Object service) {
        SERVICES.put(serviceName, service);
    }
}
