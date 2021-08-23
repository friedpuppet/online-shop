package ua.yelisieiev.persistence.jdbc;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.postgresql.ds.PGSimpleDataSource;
import ua.yelisieiev.persistence.PersistenceException;
import ua.yelisieiev.persistence.ProductPersistenceTest;

import java.sql.SQLException;

class JdbcProductPersistenceITest extends ProductPersistenceTest {
    @BeforeEach
    void createPersistence() throws PersistenceException, SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        String[] bdServers = {"instances.spawn.cc"};
        dataSource.setServerNames(bdServers);
        int[] bdPorts = {32213};
        dataSource.setPortNumbers(bdPorts);
        dataSource.setDatabaseName("foobardb");
        dataSource.setUser("spawn_admin_uBsj");
        dataSource.setPassword("a7r6UIwxa34eY0n5");

        FluentConfiguration configure = Flyway.configure();
        configure.dataSource(dataSource);
        configure.locations("/db/migration");
        Flyway flyway = configure.schemas("onlineshop").load();
        flyway.clean();
        flyway.migrate();


        setPersistence(new JdbcProductPersistence(dataSource));
    }
}