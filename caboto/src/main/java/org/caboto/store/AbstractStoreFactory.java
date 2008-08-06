package org.caboto.store;

import com.hp.hpl.jena.sdb.Store;

/**
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 */
public abstract class AbstractStoreFactory implements StoreFactory {

    public abstract Store create() throws Exception;

    public void destroy(Store store) {

        if (store != null) {
            if (store.getConnection() != null) {
                store.getConnection().close();
            }
            store.close();
        }
    }
}
