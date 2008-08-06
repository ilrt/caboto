package org.caboto.store;

import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.SDBConnection;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 */
public class StoreFactoryConnectionPoolImpl extends AbstractStoreFactory implements StoreFactory {

    StoreFactoryConnectionPoolImpl(String sdbConfigFile, DataSource dataSource)
            throws NamingException {

        this.dataSource = dataSource;

        // get the location of the assembler file
        String assemblerFilePath = this.getClass().getResource(sdbConfigFile).getPath();

        // get the store description
        storeDesc = StoreDesc.read(assemblerFilePath);
    }

    public Store create() throws SQLException {

        SDBConnection conn = SDBFactory.createConnection(dataSource.getConnection());
        return SDBFactory.connectStore(conn, storeDesc);
    }

    private DataSource dataSource;
    private StoreDesc storeDesc;
}
