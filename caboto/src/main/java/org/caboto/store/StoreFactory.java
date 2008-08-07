package org.caboto.store;

import com.hp.hpl.jena.sdb.Store;

/**
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 */
public interface StoreFactory {

    Store create();

    void destroy(Store store);

}
