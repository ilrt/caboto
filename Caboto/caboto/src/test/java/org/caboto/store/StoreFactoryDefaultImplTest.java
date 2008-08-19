package org.caboto.store;

import com.hp.hpl.jena.sdb.Store;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class StoreFactoryDefaultImplTest extends AbstractStoreFactoryTest {

    @Before
    public void setUp() throws Exception {

        // clean up the database
        super.setUp();

        storeFactory = new StoreFactoryDefaultImpl(sdbConfigFile);
        store = storeFactory.create();

    }


    @Test
    public void testCreateStore() throws Exception {

        assertEquals("The database should be derby", "derby", store.getDatabaseType().getName());
        assertNotNull("The connection to the database should not be null", store.getConnection());
    }

    @Test
    public void testDestroyStore() throws Exception {

        assertNotNull("The store should not be null", store);

        storeFactory.destroy(store);

        // there is currently no way to test if the store has been closed, but we can
        // test that the underlying database connection has been closed
        assertTrue("The SQL conection should be closed",
                store.getConnection().getSqlConnection().isClosed());

    }

    private StoreFactory storeFactory;
    private Store store;

}
