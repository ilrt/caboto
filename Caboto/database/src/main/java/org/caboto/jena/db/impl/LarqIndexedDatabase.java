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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.caboto.jena.db.Data;
import org.caboto.jena.db.DataException;
import org.caboto.jena.db.Database;
import org.caboto.jena.db.Results;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.larq.IndexBuilderModel;
import com.hp.hpl.jena.query.larq.IndexBuilderSubject;
import com.hp.hpl.jena.query.larq.IndexLARQ;
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
	
	static final Log log = LogFactory.getLog(LarqIndexedDatabase.class);
	private Database database;
	private String indexDirectory;
	private IndexBuilderModel ib;
	final private boolean cacheLARQ;
	
	public LarqIndexedDatabase(final Database db, final String indexDirectory) { this(db, indexDirectory, true, false); }
	
	public LarqIndexedDatabase(final Database db, final String indexDirectory, final boolean createIndex, final boolean cacheLARQ) {
		log.info(((createIndex)?"Creating":"Opening") + " free text index <" + indexDirectory + ">");
		this.database = db;
		this.indexDirectory = indexDirectory;
		this.cacheLARQ = cacheLARQ;
		IndexWriter indexWriter;
		try {
			FSDirectory fsd = FSDirectory.getDirectory(indexDirectory);
			indexWriter = new IndexWriter(fsd, new StandardAnalyzer(), createIndex);
		} catch (IOException e) {
			throw new RuntimeException("Error opening lucene index", e);
		}
		ib = new IndexBuilderSubject(indexWriter);
		if (createIndex) reindex();
	}
	
	public boolean addModel(String uri, Model model) {
		index(model);
		return database.addModel(uri, model);
	}

	public boolean deleteAll(String uri) {
		boolean result = database.deleteAll(uri);
		reindex();
		return result;
	}

	public boolean deleteModel(String uri, Model model) {
		unindex(model);
		return database.deleteModel(uri, model);
	}

	public Model executeConstructQuery(String sparql,
			QuerySolution initialBindings) {
		return database.executeConstructQuery(sparql, initialBindings);
	}

	public Model executeConstructQuery(Query query,
			QuerySolution initialBindings) {
		return database.executeConstructQuery(query, initialBindings);
	}

	public Results executeSelectQuery(String sparql,
			QuerySolution initialBindings) {
		log.info("Query is: \n" + sparql);
		return database.executeSelectQuery(sparql, initialBindings);
	}

	public Data getData() throws DataException {
		return database.getData();
	}
	
	public Model getUpdateModel() {
		return database.getUpdateModel();
	}

	public boolean updateProperty(String uri, String resourceUri,
			Property property, RDFNode value) {
		log.warn("UpdateProperty called. Text index will need rebuilding");
		return database.updateProperty(uri, resourceUri, property, value);
	}
	
	/**
	 * Dump the existing free-text index and recreate.
	 * 
	 * @throws IOException
	 */
	public void reindex() {
		log.info("Reindexing free text");
		ib.closeWriter();
		Results wrappedRes = 
			database.executeSelectQuery("SELECT ?s ?p ?o {{ ?s ?p ?o } UNION { GRAPH ?g { ?s ?p ?o } }}", null);
		ResultSet res = wrappedRes.getResults();
		IndexWriter indexWriter;
		try {
			FSDirectory fsd = FSDirectory.getDirectory(indexDirectory);
			indexWriter = new IndexWriter(fsd, new StandardAnalyzer(), true); // new index
		} catch (IOException e) {
			throw new RuntimeException("Error opening new index", e);
		}
		IndexBuilderModel larqBuilder = new IndexBuilderSubject(indexWriter);
		while (res.hasNext()) {
			QuerySolution soln = res.nextSolution();
			Statement s = ResourceFactory.createStatement(soln.getResource("s"),
					(Property) soln.getResource("p").as(Property.class),
					soln.get("o"));
			larqBuilder.indexStatement(s);
		}
		larqBuilder.flushWriter();
		ib = larqBuilder;
		LARQ.setDefaultIndex(cacheIfRequired(larqBuilder.getIndex()));
		log.info("Finished indexing");
	}
	
	private void index(Model model) {
		IndexBuilderModel larqBuilder = getIndexBuilder();
		larqBuilder.indexStatements(model.listStatements());
		larqBuilder.flushWriter();
		LARQ.setDefaultIndex(cacheIfRequired(larqBuilder.getIndex()));
	}
	
	/**
	 * LARQ cannot un-index statements yet, so we are very inefficient here.
	 * @param model
	 */
	private void unindex(Model model) {
		//IndexBuilderModel larqBuilder = getIndexBuilder();
		//larqBuilder.removedStatements(model.listStatements());
		//larqBuilder.flushWriter();
		//This is too slow
		//reindex();
		log.warn("Unindex for text index not possible. Reindex is needed");
	}
	
	private IndexBuilderModel getIndexBuilder() {
		if (ib == null) {
			ib = new IndexBuilderSubject(indexDirectory);
			LARQ.setDefaultIndex(cacheIfRequired(ib.getIndex()));
		}
		return ib;
	}
	
	/** This is a hack, and a thoroughly bad idea, but it will have to do **/
	public IndexLARQ cacheIfRequired(IndexLARQ index) {
		if (!cacheLARQ) return index;
		return new IndexLARQCacher(index);
	}
	
	static class IndexLARQCacher extends IndexLARQ {
		
		private final IndexLARQ index;
		@SuppressWarnings("unchecked")
		private final Map<String, List> cache = new HashMap<String, List>();
		public IndexLARQCacher(final IndexLARQ index) { super(null); this.index = index; }
		
		@SuppressWarnings("unchecked")
		@Override public Iterator search(final String searchTerm) {
			if (cache.containsKey(searchTerm)) return cache.get(searchTerm).iterator();
			log.debug("Looking for unindexed term " + searchTerm);
			final List resList = new LinkedList();
			final Iterator res = index.search(searchTerm);
			while (res.hasNext()) resList.add(res.next());
			cache.put(searchTerm, resList);
			return resList.iterator();
		}
	}
	
	public void close() {
		getIndexBuilder().closeWriter();
	}
}
