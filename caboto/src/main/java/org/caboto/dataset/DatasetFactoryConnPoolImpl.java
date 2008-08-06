package org.caboto.dataset;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.SDBConnection;

import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 */
public class DatasetFactoryConnPoolImpl implements DatasetFactory {

    public DatasetFactoryConnPoolImpl(String sdbConfigFile) throws NamingException {

        // get the location of the assembler file
        String assemblerFilePath = this.getClass().getResource(sdbConfigFile).getPath();

        // create the datasource
        Context context = (Context) new InitialContext().lookup("java:comp/env");
        dataSource = (DataSource) context.lookup("jdbc/caboto");

        storeDesc = StoreDesc.read(assemblerFilePath);
    }


    private Connection getConnection() throws SQLException {

        return dataSource.getConnection();
    }

    public Dataset create() {

        try {

            SDBConnection conn = SDBFactory.createConnection(getConnection());
            Store store = SDBFactory.connectStore(conn, storeDesc);
            Dataset ds = SDBFactory.connectDataset(store);

            return SDBFactory.connectDataset(getConnection(), storeDesc);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    DataSource dataSource;
    private Store store;
    private StoreDesc storeDesc;
}
