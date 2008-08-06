package org.caboto.store;

import com.hp.hpl.jena.sdb.Store;
import org.junit.Before;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


public class StoreFactoryConnectionPoolImplTest extends AbstractStoreFactoryTest {

    @Before
    public void setUp() throws Exception {

        // clean up the database
        super.setUp();

        // create a datasource
        DriverManagerDataSource dmds = new DriverManagerDataSource();
        dmds.setDriverClassName(jdbcDriver);
        dmds.setUrl(jdbcUrl);
        dmds.setUsername(username);
        dmds.setPassword(password);

        storeFactory = new StoreFactoryConnectionPoolImpl(sdbConfigFile, dmds);
        store = storeFactory.create();

        assertNotNull("The store should not be null", store);
    }

    public void testCreateStore() throws Exception {

        assertEquals("The connection should be open", false,
                store.getConnection().getSqlConnection().isClosed());
    }


    public void testDestroyStore() throws Exception {

        storeFactory.destroy(store);

        // there is currently no way to test if the store has been closed, but we can
        // test that the underlying database connection has been closed
        assertEquals("The connection should be closed", true,
                store.getConnection().getSqlConnection().isClosed());

    }


    private final String jdbcDriver = "org.apache.derby.jdbc.EmbeddedDriver";
    private final String jdbcUrl = "jdbc:derby:target/DB/SDB2";
    private final String username = "sa";
    private final String password = "";

    private StoreFactory storeFactory;
    private Store store;
}
