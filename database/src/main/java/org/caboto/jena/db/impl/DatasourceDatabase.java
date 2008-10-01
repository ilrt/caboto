/*
 * @(#)DatasourceDatabase.java
 * Created: 18 Sep 2008
 * Version: 1.0
 * Copyright (c) 2005-2006, University of Manchester All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the University of
 * Manchester nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.caboto.jena.db.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.caboto.jena.db.Data;
import org.caboto.jena.db.DataException;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;

/**
 * A database based on an SQL datasource
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class DatasourceDatabase extends SDBAbstractDatabase {

    // The datasource from whence to get connections
    private DataSource dataSource = null;

    // A data implementation for SDB
    private class SDBData implements Data {

        private Store store = null;

        private SDBData(Store store) {
            this.store = store;
        }

        public Dataset getDataset() {
            return SDBFactory.connectDataset(store);
        }

        public void close() {
            store.getConnection().close();
            store.close();
        }
    }

    /**
     * Creates a new DatasourceDatabase
     * @param dataSource The datasource to use for connections
     * @param dbtype The type of the database
     * @param dblayout The layout of the database
     * @throws SQLException
     */
    public DatasourceDatabase(DataSource dataSource, String dbtype,
            String dblayout) throws SQLException {
        this.dataSource = dataSource;
        Connection conn = dataSource.getConnection();
        super.init(conn, dbtype, dblayout);
    }

    /**
     * Creates a new DatasourceDatabase
     * @param dataSource The datasource to use for connections
     * @param sdbConfigFile The configuration file for the database
     * @throws SQLException
     */
    public DatasourceDatabase(DataSource dataSource, String sdbConfigFile)
            throws SQLException {
        this.dataSource = dataSource;
        Connection conn = dataSource.getConnection();
        StoreDesc storeDesc = StoreDesc.read(sdbConfigFile);
        super.init(conn, storeDesc.getDbType().getName(),
                storeDesc.getLayout().getName());
    }

    /**
     *
     * @see org.caboto.jena.db.AbstractDatabase#getData()
     */
    protected Data getData() throws DataException {
        try {
            Connection connection = dataSource.getConnection();
            return new SDBData(connectToStore(connection));
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    /**
     *
     * @see org.caboto.jena.db.AbstractDatabase#getModel(java.lang.String)
     */
    protected Model getModel(String uri) throws DataException {
        try {
            Connection connection = dataSource.getConnection();
            Store store = connectToStore(connection);
            if (uri == null) {
                return SDBFactory.connectDefaultModel(store);
            }
            return SDBFactory.connectNamedModel(store, uri);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

}
