package org.caboto.filters;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementExists;
import com.hp.hpl.jena.sparql.syntax.ElementMinus;
import com.hp.hpl.jena.sparql.syntax.ElementNotExists;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;

public class LarqAnnotationFilter extends AnnotationFilterBase implements
		AnnotationFilter {
	
	final static Node TEXTMATCH = Node.createURI("http://jena.hpl.hp.com/ARQ/property#textMatch");
	public final String searchTerm;

	public LarqAnnotationFilter(String searchParam) {
		if (searchParam.contains("\"")) throw new IllegalArgumentException("Quote mark in search param");
		this.searchTerm = searchParam;
	}
	
	@Override
	public void augmentBlock(ElementTriplesBlock arg0, String annotationBodyVar) {
		// This assumes subject indexing
		arg0.addTriple(Triple.create(Var.alloc(annotationBodyVar), TEXTMATCH, Node.createLiteral(searchTerm)));
	}

    public void visit(ElementMinus em) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
