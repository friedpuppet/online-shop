package ua.yelisieiev.dao.jdbc;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.jdbc.core.JdbcTemplate;
import ua.yelisieiev.dao.AbstractSecurityDaoTest;

class JdbcSecurityDaoTest extends AbstractSecurityDaoTest {
    @BeforeEach
    void setUp() {
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

        setDao(new JdbcSecurityDao(new JdbcTemplate(dataSource)));
    }
}