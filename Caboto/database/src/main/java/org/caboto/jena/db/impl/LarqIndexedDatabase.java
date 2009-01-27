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

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.caboto.jena.db.Data;
import org.caboto.jena.db.DataException;
import org.caboto.jena.db.Database;
import org.caboto.jena.db.Results;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.larq.IndexBuilderModel;
import com.hp.hpl.jena.query.larq.IndexBuilderSubject;
import com.hp.hpl.jena.query.larq.LARQ;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

/**
 * 
 * Wrap a database with yummy Lucene indexing.
 * 
 * @author pldms
 *
 */

public class LarqIndexedDatabase implements Database {
	
	private Database database;
	private String indexDirectory;
	private IndexBuilderModel ib;
	
	public LarqIndexedDatabase(final Database db, final String indexDirectory) throws IOException { this(db, indexDirectory, true); }
	
	public LarqIndexedDatabase(final Database db, final String indexDirectory, final boolean createIndex) throws IOException {
		this.database = db;
		this.indexDirectory = indexDirectory;
		FSDirectory fsd = FSDirectory.getDirectory(indexDirectory);
		IndexWriter indexWriter = new IndexWriter(fsd, new StandardAnalyzer(), createIndex);
		ib = new IndexBuilderSubject(indexWriter);
		if (createIndex) reindex();
	}
	
	public boolean addModel(String uri, Model model) {
		System.err.println("!!!!! Adding model:\n" + model);
		index(model);
		return database.addModel(uri, model);
	}

	public boolean deleteAll(String uri) {
		System.err.println("!!!! !!!");
		return database.deleteAll(uri);
	}

	public boolean deleteModel(String uri, Model model) {
		System.err.println("!!!! !!! --");
		unindex(model);
		return database.deleteModel(uri, model);
	}

	public Model executeConstructQuery(String sparql,
			QuerySolution initialBindings) {
		System.err.println("!!!! !!! con");
		return database.executeConstructQuery(sparql, initialBindings);
	}

	public Model executeConstructQuery(Query query,
			QuerySolution initialBindings) {
		System.err.println("!!!! Query\n" + query);
		try {
			reindex();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return database.executeConstructQuery(query, initialBindings);
	}

	public Results executeSelectQuery(String sparql,
			QuerySolution initialBindings) {
		System.err.println("!!!! !!!");
		return database.executeSelectQuery(sparql, initialBindings);
	}

	public Data getData() throws DataException {
		System.err.println("!!!! !!! data");
		return new DataWrapper(database.getData());
	}
	
	class DataWrapper implements Data {
		
		private Data data;

		public DataWrapper(final Data data) {
			this.data = data;
		}
		
		public void close() {
			data.close();
		}

		public Dataset getDataset() {
			System.err.println("Get ds");
			return data.getDataset();
		}

		public Model getModel(String uri) {
			System.err.println("GET MODEL!!");
			Model m = data.getModel(uri);
			m.register(ib);
			return m;
		}
		
	}
	
	public Model getUpdateModel() {
		return database.getUpdateModel();
	}

	public boolean updateProperty(String uri, String resourceUri,
			Property property, RDFNode value) {
		System.err.println("!!!! !!! up");
		return database.updateProperty(uri, resourceUri, property, value);
	}
	
	/**
	 * Dump the existing free-text index and recreate.
	 * 
	 * @throws IOException
	 */
	public void reindex() throws IOException {
		ib.closeWriter();
		Results wrappedRes = 
			database.executeSelectQuery("SELECT ?s ?p ?o {{ ?s ?p ?o } UNION { GRAPH ?g { ?s ?p ?o } }}", null);
		ResultSet res = wrappedRes.getResults();
		FSDirectory fsd = FSDirectory.getDirectory(indexDirectory);
		IndexWriter indexWriter = new IndexWriter(fsd, new StandardAnalyzer(), true); // new index
		IndexBuilderModel larqBuilder = new IndexBuilderSubject(indexWriter);
		while (res.hasNext()) {
			QuerySolution soln = res.nextSolution();
			Statement s = ResourceFactory.createStatement(soln.getResource("s"),
					(Property) soln.getResource("p").as(Property.class),
					soln.get("o"));
			System.err.println("!! St:" + s);
			larqBuilder.indexStatement(s);
		}
		larqBuilder.flushWriter();
		ib = larqBuilder;
		System.err.println("Contains title1?" + larqBuilder.getIndex().hasMatch("title1"));
		LARQ.setDefaultIndex(larqBuilder.getIndex());
	}
	
	private void index(Model model) {
		IndexBuilderModel larqBuilder = getIndexBuilder();
		larqBuilder.indexStatements(model.listStatements());
		larqBuilder.flushWriter();
		LARQ.setDefaultIndex(larqBuilder.getIndex());
	}
	
	private void unindex(Model model) {
		IndexBuilderModel larqBuilder = getIndexBuilder();
		larqBuilder.removedStatements(model.listStatements());
		larqBuilder.flushWriter();
	}
	
	private IndexBuilderModel getIndexBuilder() {
		if (ib == null) {
			ib = new IndexBuilderSubject(indexDirectory);
			LARQ.setDefaultIndex(ib.getIndex());
		}
		return ib;
	}

	public void close() {
		getIndexBuilder().closeWriter();
	}
}
