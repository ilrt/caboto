package org.caboto.filters;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.syntax.TripleCollector;

public class LarqAnnotationFilter extends AnnotationFilterBase implements
		AnnotationFilter {
	
	final static Node TEXTMATCH = Node.createURI("http://jena.hpl.hp.com/ARQ/property#textMatch");
	public final String searchTerm;

	public LarqAnnotationFilter(String searchParam) {
		if (searchParam.contains("\"")) throw new IllegalArgumentException("Quote mark in search param");
		this.searchTerm = searchParam;
	}
	
	@Override
	public void augmentBlock(TripleCollector arg0, String annotationBodyVar, String annotationHeadVar) {
		// This assumes subject indexing
		arg0.addTriple(Triple.create(Var.alloc(annotationBodyVar), TEXTMATCH, Node.createLiteral(searchTerm)));
	}


}
