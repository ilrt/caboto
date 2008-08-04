package org.caboto.dataset;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 */
public class DefaultFactoryImpl implements DatasetFactory {

    public DefaultFactoryImpl(String sdbConfigFile) {
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
    public String sdbConfigFile;
}
