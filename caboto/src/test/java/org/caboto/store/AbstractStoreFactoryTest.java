package org.caboto.store;

import junit.framework.TestCase;
import org.junit.Before;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 */
public abstract class AbstractStoreFactoryTest extends TestCase {

    @Before
    public void setUp() throws Exception {

        // format the store
        StoreInitializer storeInitializer = new StoreInitializer(formatConfigFile,
                formatPropertyKey, sdbConfigFile);
        storeInitializer.initializeStore();
    }


    final String sdbConfigFile = "/sdb.ttl";
    private final String formatConfigFile = "/startup.properties";
    private final String formatPropertyKey = "sdb.store.formatted";

}
