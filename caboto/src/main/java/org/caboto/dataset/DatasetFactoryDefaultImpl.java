package org.caboto.dataset;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;

/**
 * A basic factory that uses an assember to obtain all of its sdb store details.
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 */
public class DatasetFactoryDefaultImpl implements DatasetFactory {

    public DatasetFactoryDefaultImpl(String sdbConfigFile) {
        this.sdbConfigFile = sdbConfigFile;
        initStore();
    }

    private void initStore() {

        String storePath = this.getClass().getResource(sdbConfigFile).getPath();
        store = SDBFactory.connectStore(storePath);
    }

    public Dataset create() {
        return SDBFactory.connectDataset(store);
    }

    private Store store;
    private String sdbConfigFile;
}
