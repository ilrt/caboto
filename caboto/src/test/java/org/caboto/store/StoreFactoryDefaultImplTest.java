package org.caboto.store;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.apache.commons.configuration.ConfigurationException;
import com.hp.hpl.jena.sdb.Store;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 */
public class StoreFactoryDefaultImplTest extends TestCase {

    @Before
    public void setUp() throws ConfigurationException {

        // format the store
        StoreInitializer storeInitializer = new StoreInitializer(formatConfigFile,
                formatPropertyKey, sdbConfigFile);
        storeInitializer.initializeStore();
    }

    @Test
    public void testCreateStore() throws Exception {

        StoreFactory storeFactory = new StoreFactoryDefaultImpl(sdbConfigFile);
        Store store = storeFactory.create();

        assertEquals("The database should be derby", "derby", store.getDatabaseType().getName());
        assertNotNull("The connection to the database should not be null", store.getConnection());
    }

    @Test
    public void testDestroyStore() throws Exception {

        StoreFactory storeFactory = new StoreFactoryDefaultImpl(sdbConfigFile);
        Store store = storeFactory.create();

        assertNotNull("The store should not be null", store);

        storeFactory.destroy(store);

        // there is currently no way to test if the store has been closed, but we can
        // test that the underlying database connection has been closed
        assertTrue("The SQL conection should be closed",
                store.getConnection().getSqlConnection().isClosed());

    }

    private String sdbConfigFile = "/sdb.ttl";
    private String formatConfigFile = "/startup.properties";
    private String formatPropertyKey = "sdb.store.formatted";
}
