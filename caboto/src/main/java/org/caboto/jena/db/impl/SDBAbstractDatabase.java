/*
 * @(#)SDBAbstractDatabase.java
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.caboto.jena.db.AbstractDatabase;

import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.SDBConnection;

/**
 * An abstract database for using SDB
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public abstract class SDBAbstractDatabase extends AbstractDatabase {

    // The current version of the database
    private static final float VERSION = 0.1f;

    // The store description
    private StoreDesc storeDesc = null;

    /**
     * Initialises the database
     *
     * @param sqlConn An SQL connection to the underlying database
     * @param dbtype The database type
     * @param dblayout The database layout
     * @throws SQLException
     */
    protected void init(Connection sqlConn, String dbtype,
            String dblayout) throws SQLException {
        this.storeDesc = new StoreDesc(dblayout, dbtype);
        SDBConnection conn = new SDBConnection(sqlConn);
        Statement statement = sqlConn.createStatement();
        try {
            ResultSet results = statement.executeQuery(
                    "SELECT * FROM crew_version");
            if (results.next()) {
                float version = results.getFloat("version");
                if (version > VERSION) {
                    throw new RuntimeException (
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

            Store store = SDBFactory.connectStore(conn, storeDesc);
            store.getTableFormatter().format();
            store.close();
            }
        }

    // Upgrades the database to the current version
    private void upgradeDB(float oldVersion, float newVersion)
            throws SQLException {
        throw new SQLException("First version cannot upgrade");
    }

    /**
     * Connects to the store
     * @param conn The connection
     * @return The store
     */
    protected Store connectToStore(Connection conn) {
        return SDBFactory.connectStore(conn, storeDesc);
    }
}
