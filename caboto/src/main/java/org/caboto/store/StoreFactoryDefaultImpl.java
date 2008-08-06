package org.caboto.store;

import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 */
public class StoreFactoryDefaultImpl extends AbstractStoreFactory implements StoreFactory {


    public StoreFactoryDefaultImpl(String sdbConfigFile) {
        assemblerFilePath = this.getClass().getResource(sdbConfigFile).getPath();
    }

    public Store create() throws Exception {
        return SDBFactory.connectStore(assemblerFilePath);
    }

    private final String assemblerFilePath;
}
