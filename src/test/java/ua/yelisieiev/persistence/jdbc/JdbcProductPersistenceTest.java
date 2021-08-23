package ua.yelisieiev.persistence.jdbc;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import ua.yelisieiev.persistence.PersistenceException;
import ua.yelisieiev.persistence.ProductPersistenceTest;

class JdbcProductPersistenceTest extends ProductPersistenceTest {
    @BeforeEach
    void createPersistence() throws PersistenceException {
//        System.out.println(System.getProperty("java.class.path"));
        // H2
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        dataSource.setUser("root");
        dataSource.setPassword("GOD");

        FluentConfiguration configure = Flyway.configure();
        configure.dataSource(dataSource);
        configure.locations("/db/testmigration/");
        Flyway flyway = configure.schemas("onlineshop").load();
        flyway.clean();
        flyway.migrate();

        setPersistence(new JdbcProductPersistence(dataSource));
    }
}