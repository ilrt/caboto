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
