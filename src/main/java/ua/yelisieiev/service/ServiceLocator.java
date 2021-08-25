package ua.yelisieiev.service;

import org.postgresql.ds.PGSimpleDataSource;
import ua.yelisieiev.dao.DaoException;
import ua.yelisieiev.dao.ProductDao;
import ua.yelisieiev.dao.jdbc.JdbcProductDao;

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

        ProductDao productDao = null;
        try {
            productDao = new JdbcProductDao(dataSource);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
        ProductsService productsService = new ProductsService(productDao);
        addService(ProductsService.class, productsService);

//        SecurityService securityService = new SecurityService(dataSource);
//        addService(SecurityService.class, securityService);
    }

    public static <T> T getService(Class<T> serviceType) {
        return serviceType.cast(SERVICES.get(serviceType));
    }

    public static void addService(Class<?> serviceName, Object service) {
        SERVICES.put(serviceName, service);
    }
}
