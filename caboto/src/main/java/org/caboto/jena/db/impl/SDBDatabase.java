/*
 * @(#)Database.java
 * Created: 20 Aug 2008
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

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Properties;

import org.caboto.jena.db.AbstractDatabase;
import org.caboto.jena.db.Data;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.JDBC;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.store.DatabaseType;

/**
 * Represents access to the SDB database
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class SDBDatabase extends AbstractDatabase {

    // The current version of the database
    private static final float VERSION = 0.1f;

    private StoreDesc storeDesc = null;

    private String jdbcUrl = null;

    private String username = null;

    private String password = null;

    private LinkedList<Store> stores =
        new LinkedList<Store>();

    private int maxConnections = 0;

    private int noConnections = 0;

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
            synchronized (stores) {
                stores.addLast(store);
                stores.notifyAll();
            }
        }
    }

    /**
     * Creates a database
     *
     * The database configuration file should contain (replacing {prefix} with
     * the value passed in configPrefix):
     *     {prefix}.jdbcUrl  - The JDBC url to connect to
     *     {prefix}.username - The username to connect with (optional)
     *     {prefix}.password - The password to connect with (optional)
     *     {prefix}.dbtype   - The Jena SDB DatabaseType of the database
     *     {prefix}.dblayout - The Jena SDB LayoutType of the database
     *
     * The database will store the current version of the data and will
     * contain if the database has been initialised.  If the data indicates that
     * it has not been initialised, initialisation will be performed, which may
     * overwrite existing data in the database.
     *
     * @param configPrefix The prefix to the configuration to use
     * @param dbConfigFile The database configuration file
     *                         location relative to the classpath
     * @throws IOException if there is an error loading the configuration file
     * @throws SQLException if there is an error handling the database
     *
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

        init(jdbc, user, pass, dbtype, dblayout, 0);
    }

    /**
     * Creates a new SDBDatabase
     *
     * @param jdbcUrl The url of the database
     * @param username The username used to access the database
     * @param password The password used to access the database
     * @param dbtype The SDB DatabaseType of the database
     * @param dblayout The SDB DatabaseLayout of the database
     *
     * @throws IOException if there is an error loading the configuration file
     * @throws SQLException if there is an error handling the database
     */
    public SDBDatabase(String jdbcUrl, String username, String password,
            String dbtype, String dblayout) throws SQLException, IOException {
        init(jdbcUrl, username, password, dbtype, dblayout, 0);
    }

    private void init(String jdbcUrl, String username, String password,
            String dbtype, String dblayout, int maxConnections)
            throws SQLException, IOException {

        // Load the driver
        String driver = JDBC.getDriver(DatabaseType.fetch(dbtype));
        JDBC.loadDriver(driver);

        // Store db parameters
        storeDesc = new StoreDesc(dblayout, dbtype);
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.maxConnections = maxConnections;

        // Check if the jena tables have been created
        SDBConnection conn = new SDBConnection(jdbcUrl, username, password);
        Connection sqlConn = conn.getSqlConnection();
        Statement statement = sqlConn.createStatement();
        try {
            ResultSet results = statement.executeQuery(
                    "SELECT * FROM crew_version");
            if (results.next()) {
                float version = results.getFloat("version");
                if (version > VERSION) {
                    throw new IOException(
                            "Cannot downgrade database (current = "
                            + VERSION + " db = " + version);
                } else if (version < VERSION) {
                    upgradeDB(version, VERSION);
                }
            } else {
                throw new SQLException();
            }
        } catch (SQLException e) {

            // If we are here, we should try to create the version table
            // and initialise the store
            statement.execute(
                    "CREATE TABLE crew_version (version decimal(5, 5))");
            statement.execute(
                    "INSERT INTO crew_version VALUES (" + VERSION + ")");

            synchronized (stores) {
                Store store = SDBFactory.connectStore(conn, storeDesc);
                store.getTableFormatter().format();
                stores.add(store);
                stores.notifyAll();
            }
        }
    }

    private Store getStore() {
        Store store = null;
        synchronized (stores) {
            if (stores.isEmpty()) {
                if ((maxConnections > 0) && (noConnections >= maxConnections)) {
                    while (stores.isEmpty()) {
                        try {
                            stores.wait();
                        } catch (InterruptedException e) {
                            // Do Nothing
                        }
                    }
                } else {
                    SDBConnection conn =
                        new SDBConnection(jdbcUrl, username, password);
                    stores.addLast(SDBFactory.connectStore(conn, storeDesc));
                }
            }
            store = stores.removeFirst();
        }
        return store;
    }

    // Upgrades the database to the current version
    private void upgradeDB(float oldVersion, float newVersion)
            throws SQLException {
        throw new SQLException("First version cannot upgrade");
    }

    /**
     *
     * @see org.caboto.jena.db.AbstractDatabase#getData()
     */
    protected Data getData() {
        Store store = getStore();
        return new SDBData(store);
    }

    /**
     *
     * @see org.caboto.jena.db.AbstractDatabase#getModel(java.lang.String)
     */
    protected Model getModel(String uri) {
        Store store = getStore();
        if (uri == null) {
            return SDBFactory.connectDefaultModel(store);
        }
        return SDBFactory.connectNamedModel(store, uri);
    }
}
