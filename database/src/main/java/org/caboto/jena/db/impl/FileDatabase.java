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
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.util.Context;

import org.caboto.jena.db.AbstractDatabase;
import org.caboto.jena.db.Data;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * A database represented by a set of files
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: FileDatabase.java 820 2008-08-21 09:46:04Z arowley $
 */
public class FileDatabase extends AbstractDatabase {

    // The dataset of files
    private Dataset dataset = null;

    // A data implementation for the file dataset
    private class FileData implements Data {
    	
    	private Context context;

		public FileData(Context context) {
    		this.context = context;
    	}
    	
        public Dataset getDataset() {
            return dataset;
        }

        public Model getModel(String uri) {
            return ModelFactory.createDefaultModel();
        }

        public void close() {
            // Does Nothing
        }

		public Context getContext() {
			return context;
		}
    }

    /**
     * Creates a new FileDatabase
     *
     * @param defaultModelFile path to the file holding the default model.
     * @param namedGraphsDir   path to the directory holding the named graphs.
     * @throws IOException if the datasets cannot be loaded
     */
    public FileDatabase(final String defaultModelFile,
                        final String namedGraphsDir) throws IOException {

        try {
            // get the default model
             File aDefaultModelFile = new File(URLDecoder.decode(
                    getClass().getResource(defaultModelFile).getPath(),
                    "UTF-8"));

            if (!aDefaultModelFile.exists() || !aDefaultModelFile.isFile()) {
                throw new IOException(new StringBuilder()
                        .append(aDefaultModelFile.getAbsolutePath())
                        .append(" - The file specified as the default model is either ")
                        .append("does not exist or is not a file").toString());
            }


            // find the named graphs
            File namedGraphsPath = new File(URLDecoder.decode(
                    getClass().getResource(namedGraphsDir).getPath(), "UTF-8"));

            if (!namedGraphsPath.exists() || !namedGraphsPath.isDirectory()) {
                throw new IOException(new StringBuilder()
                        .append("The directory specified for the named graphs is ")
                        .append("either does not exist or is not a directory").toString());
            }


            List<String> namedGraphsList = new ArrayList<String>();

            File[] namedGraphFiles = namedGraphsPath.listFiles(new RdfFileFilter());

            for (File namedGraphFile : namedGraphFiles) {
                namedGraphsList.add(namedGraphFile.getAbsolutePath());
            }

            dataset = com.hp.hpl.jena.query.DatasetFactory
                    .create(aDefaultModelFile.getAbsolutePath(), namedGraphsList);

        } catch (UnsupportedEncodingException e) {
            // Not Going to happen!
            e.printStackTrace();
        }
    }

    /**
     * @see org.caboto.jena.db.AbstractDatabase#getData()
     */
    public Data getData() {
        return new FileData(getQueryContext());
    }
}
