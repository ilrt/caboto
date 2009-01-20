package org.caboto.jena.db.impl;

import java.io.File;
import java.io.IOException;

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
import com.hp.hpl.jena.query.larq.IndexBuilder;
import com.hp.hpl.jena.query.larq.IndexBuilderModel;
import com.hp.hpl.jena.query.larq.IndexBuilderString;
import com.hp.hpl.jena.query.larq.IndexBuilderSubject;
import com.hp.hpl.jena.query.larq.LARQ;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class LarqIndexedDatabase implements Database {
	
	private Database database;
	private File indexDirectory;
	private IndexBuilderModel ib;
	
	public LarqIndexedDatabase(final Database db, final File indexDirectory) throws IOException { this(db, indexDirectory, true); }
	
	public LarqIndexedDatabase(final Database db, final File indexDirectory, final boolean createIndex) throws IOException {
		this.database = db;
		this.indexDirectory = indexDirectory;
		FSDirectory fsd = FSDirectory.getDirectory(indexDirectory);
		IndexWriter indexWriter = new IndexWriter(fsd, new StandardAnalyzer(), createIndex);
		ib = new IndexBuilderSubject(indexWriter);
		if (createIndex) reindex();
	}
	
	public boolean addModel(String uri, Model model) {
		index(model);
		return database.addModel(uri, model);
	}

	public boolean deleteAll(String uri) {
		return database.deleteAll(uri);
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
		return database.updateProperty(uri, resourceUri, property, value);
	}
	
	public void reindex() {
		Results wrappedRes = 
			database.executeSelectQuery("SELECT ?s ?p ?o {{ ?s ?p ?o } UNION { GRAPH ?g { ?s ?p ?o } }}", null);
		ResultSet res = wrappedRes.getResults();
		// TODO delete existing index
		IndexBuilderModel larqBuilder = getIndexBuilder();
		while (res.hasNext()) {
			QuerySolution soln = res.nextSolution();
			Statement s = ResourceFactory.createStatement(soln.getResource("s"),
					(Property) soln.getResource("p").as(Property.class),
					soln.get("o"));
			larqBuilder.indexStatement(s);
		}
		larqBuilder.flushWriter();
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
