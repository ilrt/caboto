/*
 * Copyright (c) 2008, University of Bristol
 * Copyright (c) 2008, University of Manchester
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the names of the University of Bristol and the
 *    University of Manchester nor the names of their
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
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
 *
 */
package org.caboto.jena.db.impl;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.JDBC;
import com.hp.hpl.jena.sdb.sql.SDBConnectionDesc;
import com.hp.hpl.jena.sdb.store.DatabaseType;
import com.hp.hpl.jena.sparql.util.Context;

import org.caboto.jena.db.Data;
import org.caboto.jena.db.DataException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Represents access to the SDB database
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class SDBDatabase extends SDBAbstractDatabase {

    // The JDBC URL
    private String jdbcUrl = null;

    // The username
    private String username = null;

    // The password
    private String password = null;


    // A data implementation for SDB
    private class SDBData implements Data {

        private Store store = null;
		private Context context;

        private SDBData(Store store, Context context) {
            this.store = store;
            this.context = context;
        }

        public Dataset getDataset() {
            return SDBFactory.connectDataset(store);
        }

        public Model getModel(String uri) {
            if (uri == null) {
                return SDBFactory.connectDefaultModel(store);
            }
            return SDBFactory.connectNamedModel(store, uri);
        }

        public void close() {
            store.close();
            store.getConnection().close();
        }

		public Context getContext() {
			return context;
		}
    }

    /**
     * Creates a database
     * <p/>
     * The database configuration file should contain (replacing {prefix} with
     * the value passed in configPrefix):
     * {prefix}.jdbcUrl  - The JDBC url to connect to
     * {prefix}.username - The username to connect with (optional)
     * {prefix}.password - The password to connect with (optional)
     * {prefix}.dbtype   - The Jena SDB DatabaseType of the database
     * {prefix}.dblayout - The Jena SDB LayoutType of the database
     * <p/>
     * The database will store the current version of the data and will
     * contain if the database has been initialised.  If the data indicates that
     * it has not been initialised, initialisation will be performed, which may
     * overwrite existing data in the database.
     *
     * @param configPrefix The prefix to the configuration to use
     * @param dbConfigFile The database configuration file
     *                     location relative to the classpath
     * @throws IOException  if there is an error loading the configuration file
     * @throws SQLException if there is an error handling the database
     */
    public SDBDatabase(String configPrefix, String dbConfigFile)
            throws IOException, SQLException {

        // Get the properties
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream(dbConfigFile));
        String jdbc = properties.getProperty(configPrefix + ".jdbcUrl");
        String user = properties.getProperty(configPrefix + ".username");
        String pass = properties.getProperty(configPrefix + ".password");
        String dbtype = properties.getProperty(configPrefix + ".dbtype");
        String dblayout = properties.getProperty(configPrefix + ".dblayout");

        init(jdbc, user, pass, dbtype, dblayout);
    }

    /**
     * Creates a new SDBDatabase
     *
     * @param jdbcUrl  The url of the database
     * @param username The username used to access the database
     * @param password The password used to access the database
     * @param dbtype   The SDB DatabaseType of the database
     * @param dblayout The SDB DatabaseLayout of the database
     * @throws SQLException if there is an error handling the database
     */
    public SDBDatabase(String jdbcUrl, String username, String password,
                       String dbtype, String dblayout) throws SQLException {
        init(jdbcUrl, username, password, dbtype, dblayout);
    }

    /**
     * Creates an SDBDatabase from an SDB config file
     *
     * @param sdbConfigFile The path to the config file
     * @throws SQLException if there is an error.
     */
    public SDBDatabase(String sdbConfigFile) throws SQLException {
        Model ttl = ModelFactory.createDefaultModel();
        InputStream input = getClass().getResourceAsStream(sdbConfigFile);
        if (input == null) {
            throw new SQLException("Config file " + sdbConfigFile
                    + " not found in classpath");
        }
        ttl.read(input, null, "TTL");
        StoreDesc storeDesc = StoreDesc.read(ttl);
        SDBConnectionDesc desc = storeDesc.connDesc;
        init(desc.getJdbcURL(), desc.getUser(), desc.getPassword(),
                storeDesc.getDbType().getName(),
                storeDesc.getLayout().getName());
    }

    private void init(String jdbcUrl, String username, String password,
                      String dbtype, String dblayout)
            throws SQLException {

        // Load the driver
        String driver = JDBC.getDriver(DatabaseType.fetch(dbtype));
        JDBC.loadDriver(driver);

        // Get a connection
        Connection sqlConn = DriverManager.getConnection(jdbcUrl, username,
                password);
        super.init(sqlConn, dbtype, dblayout);
        sqlConn.close();

        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    private Store getStore() throws SQLException {
        Connection sqlConn = DriverManager.getConnection(jdbcUrl, username,
                password);
        return connectToStore(sqlConn);
    }

    /**
     * @see org.caboto.jena.db.AbstractDatabase#getData()
     */
    public Data getData() throws DataException {
        try {
            return new SDBData(getStore(), getQueryContext());
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    /**
     * Removes everything from the database
     * @throws SQLException
     */
    public void clean() throws SQLException {
        getStore().getTableFormatter().format();
    }
}
