package org.caboto.jena.db.impl;

import java.io.File;

import com.hp.hpl.jena.query.larq.IndexBuilderModel;
import com.hp.hpl.jena.query.larq.IndexBuilderSubject;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

public class LarqCheck {

	private static IndexBuilderModel ib;
	
	public static void main(String[] args) {
		String file = "/tmp/test-larq";
		//File indexDir = new File("/tmp/test-larq");
		Statement s1 = ResourceFactory.createStatement(RDF.Alt, RDF.first, ResourceFactory.createPlainLiteral("one"));
		Statement s2 = ResourceFactory.createStatement(RDF.Bag, RDF.rest, ResourceFactory.createPlainLiteral("two"));
		
		ib = new IndexBuilderSubject(file);//indexDir);
		ib.indexStatement(s1);
		ib.flushWriter();
		//ib.closeWriter();
		
		showSituation();
		
		ib = new IndexBuilderSubject(file);//indexDir);
		ib.indexStatement(s2);
		ib.closeWriter();
		
		showSituation();
	}
	
	private static int count = 1;
	
	public static void showSituation() {
		System.err.printf("(%s)\t%s\t%s\n",
				count++,
				ib.getIndex().hasMatch("one"),
				ib.getIndex().hasMatch("two"));
	}

}
