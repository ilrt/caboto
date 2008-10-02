package org.caboto.jena.db.impl;

import java.io.FileFilter;
import java.io.File;

/**
 * <p>File filter for RDF files.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: RdfFileFilter.java 547 2008-01-18 09:34:27Z cmmaj $
 */
public class RdfFileFilter implements FileFilter {
    public boolean accept(File file) {
        return file.getName().toLowerCase().endsWith(".rdf");
    }
}
