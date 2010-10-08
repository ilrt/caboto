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
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import org.caboto.jena.db.AbstractDatabase;
import org.caboto.jena.db.DataException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * An abstract database for using SDB
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public abstract class SDBAbstractDatabase extends AbstractDatabase {

    // The store description
    private StoreDesc storeDesc = null;

    /**
     * Initialises the database
     *
     * @param sqlConn An SQL connection to the underlying database
     * @param dbtype The database type
     * @param dblayout The database layout
     * @throws SQLException if there is an error
     */
    protected void init(Connection sqlConn, String dbtype,
            String dblayout) throws SQLException {
        this.storeDesc = new StoreDesc(dblayout, dbtype);
        SDBConnection conn = new SDBConnection(sqlConn);
        Store store = SDBFactory.connectStore(conn, storeDesc);
        if (!Scratch.formatted(store)) {
            store.getTableFormatter().create();
        }
        store.close();
    }

    /**
     * Connects to the store
     * @param conn The connection
     * @return The store
     */
    protected Store connectToStore(Connection conn) {
        return SDBFactory.connectStore(conn, storeDesc);
    }

    /**
     * Imports a graph from file(s) in the classpath
     * @param files The name(s) of the file to import
     */
    public void setLoadedFiles(String... files) {
        for (String filename : files) {
            Model model = getUpdateModel();
            InputStream input = getClass().getResourceAsStream(filename);
            model.read(input, "RDF/XML");
            URL url = getClass().getResource(filename);
            addModel(url.toString(), model);
        }
    }
}
