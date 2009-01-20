package org.caboto.jena.db.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.caboto.jena.db.Database;
import org.caboto.jena.db.Results;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDFS;

public class LarqIndexedDatabaseTest {

	private LarqIndexedDatabase indexedDb;
	private FileDatabase db;
	private File ldir;

	@Before
	public void setUp() throws Exception {
		db = new FileDatabase("/empty.n3","/graphs/test");
		ldir = new File(System.getProperty("java.io.tmpdir"),"larq-test");
		if (ldir.exists()) remove(ldir);
		if (!ldir.mkdirs()) throw new Error("Can't create " + ldir);
		ldir.deleteOnExit();
		indexedDb = new LarqIndexedDatabase(db,ldir);
	}

	private void remove(File file) {
		if (!file.exists()) return;
		if (!file.isDirectory()) { file.delete(); return; }
		// Directory: remove contents, then self
		for (File subFile: file.listFiles()) remove(subFile);
		file.delete();
	}
	
	@After
	public void tearDown() throws Exception {
		// Close??
	}
	
	@Test
	public void testBasics() throws IOException {
		assertEquals("Files were indexed",ResourceFactory.createResource("http://example.com/foo/1"),find("tester"));
		// reopen!
		indexedDb.close();
		indexedDb = new LarqIndexedDatabase(db,ldir,false);
		assertEquals("Files were still indexed",ResourceFactory.createResource("http://example.com/foo/1"),find("tester"));
	}
	
	@Test
	public void testAddModel() {
		assertNull("Unique ain't there", find("unique"));
		Model m = ModelFactory.createDefaultModel();
		m.add(m.createResource("http://example.com/unique"), RDFS.label, "unique");
		indexedDb.addModel("http://example.com/foo", m);
		assertEquals("Unique is there", ResourceFactory.createResource("http://example.com/unique"), find("unique"));
		m = ModelFactory.createDefaultModel();
		m.add(m.createResource("http://example.com/unique2"), RDFS.label, "unique2");
		indexedDb.addModel("http://example.com/bar", m);
		assertEquals("Unique is still there", ResourceFactory.createResource("http://example.com/unique"), find("unique"));
		assertEquals("Unique2 is now there", ResourceFactory.createResource("http://example.com/unique2"), find("unique2"));
	}

	@Test
	public void testDeleteAll() {
		
	}

	@Test
	public void testDeleteModel() {
		//fail("Not yet implemented");
	}

	@Test
	public void testExecuteSelectQuery() {
		//fail("Not yet implemented");
	}

	@Test
	public void testUpdateProperty() {
		//fail("Not yet implemented");
	}
	
	private Resource find(String string) {
		String query = "SELECT ?res { ?res <http://jena.hpl.hp.com/ARQ/property#textMatch> \"" + string + "\" }";
		Results result = indexedDb.executeSelectQuery(query, null);
		ResultSet realRes = result.getResults();
		Resource res = (realRes.hasNext()) ?
			realRes.nextSolution().getResource("res") :
		    null ;
		result.close();
		return res;
	}
}
