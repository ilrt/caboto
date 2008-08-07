package org.caboto.store;

import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.SDBConnection;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 */
public class StoreFactoryConnectionPoolImpl extends AbstractStoreFactory implements StoreFactory {

    StoreFactoryConnectionPoolImpl(String sdbConfigFile, DataSource dataSource) {

        this.dataSource = dataSource;

        // get the location of the assembler file
        String assemblerFilePath = this.getClass().getResource(sdbConfigFile).getPath();

        // get the store description
        storeDesc = StoreDesc.read(assemblerFilePath);
    }

    public Store create() {

        try {
            SDBConnection conn = SDBFactory.createConnection(dataSource.getConnection());
            return SDBFactory.connectStore(conn, storeDesc);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private final DataSource dataSource;
    private final StoreDesc storeDesc;
}
