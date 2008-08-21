/*
 * Copyright (c) 2008, University of Bristol
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
 * 3) Neither the name of the University of Bristol nor the names of its
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
package org.caboto.servlet;

import java.net.URLDecoder;

import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.store.StoreFormatter;
import org.apache.commons.configuration.PropertiesConfiguration;

import javax.servlet.http.HttpServlet;

/**
 * <p>This servlet ensures that the system is correctly initiated.</p>
 *
 * <p>The Caboto project uses an RDF store that is backed by a relational database. The
 * unerlying implementation uses Jena SDB which means we need to ensure that the database
 * is correctly formated before it is used. The servlet has three initialization
 * parameters:</p>
 *
 * <ul>
 *  <li><em>configFile</em> -  a configuration file that specifes whether or not the database
 *      has already been formatted.</li>
 *  <li><em>formatProps</em> - the property name used to hold a "true" or "false" value for
 *      indicating if the database has been formatted.</li>
 *  <li><em>configFile</em> - a configuration file that holds the database details.</li>
 * </ul>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: CabotoStartupServlet.java 177 2008-05-30 13:50:59Z mike.a.jones $
 *
 **/
public class CabotoStartupServlet extends HttpServlet {


    /**
     * <p>Initialize the system on servlet start up.</p>
     */
    public final void init() {

        // pull in init parameter values
        String configFile = getInitParameter("configFile");
        String formatProps = getInitParameter("formatProps");
        String sdbConfigFile = getInitParameter("sdbConfigFile");

        try {

            // load the properties file
            String path = URLDecoder.decode(
                    getClass().getResource(configFile).getPath(), "UTF-8");
            PropertiesConfiguration config = new PropertiesConfiguration(path);

            // format database tables if necessary
            if (!config.getBoolean(formatProps)) {

                String storePath = URLDecoder.decode(
                        this.getClass().getResource(sdbConfigFile).getPath(),
                        "UTF-8");
                Store store = SDBFactory.connectStore(storePath);
                StoreFormatter storeFormatter = store.getTableFormatter();
                storeFormatter.format();

                // update the config file
                config.setProperty(formatProps, true);
                config.save();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
