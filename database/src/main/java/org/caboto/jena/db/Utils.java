/*
 * Copyright (c) 2008, University of Manchester
 * Copyright (c) 2008, University of Bristol
 * All rights reserved.
 *
 * See LICENCE in root directory of source code for details of the license.
 */

package org.caboto.jena.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Database utilities
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class Utils {

    private Utils() {
        // Does Nothing
    }

    /**
     * <p>A utility method that loads a SPARQL query and places the
     *    contents in a <code>String</code>
     *
     * @param input The input stream to read
     * @return a <code>String</code> holding the SPARQL
     */
    public static String loadSparql(final InputStream input) {
        StringBuffer buffer = new StringBuffer();

        try {
            BufferedReader d = new BufferedReader(new InputStreamReader(input));

            String s;
            while ((s = d.readLine()) != null) {
                buffer.append(s);
                buffer.append("\n");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return buffer.toString();
    }

    /**
     * Loads sparql from the classpath of a given class
     * @param path The path to load from
     * @param cls The class to load using
     * @return The sparql query
     */
    public static String loadSparql(final String path, final Class<?> cls) {
        return loadSparql(cls.getResourceAsStream(path));
    }

    /**
     * Loads sparql from a classpath relative to an object
     * @param path The path to load from
     * @param object The object to load relative to
     * @return The spaql query
     */
    public static String loadSparql(final String path, final Object object) {
        return loadSparql(path, object.getClass());
    }

    /**
     * Loads sparql from the classpath of this class
     * @param path The path to load from
     * @return The sparql query
     */
    public static String loadSparql(final String path) {
        return loadSparql(path, Utils.class);
    }
}
