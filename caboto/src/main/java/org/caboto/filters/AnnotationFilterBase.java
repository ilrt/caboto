/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.caboto.filters;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.syntax.*;

import java.util.List;

/**
 *
 * @author pldms
 */
public abstract class AnnotationFilterBase extends ElementVisitorBase
        implements AnnotationFilter, ElementVisitor {

    private String annotationBodyVar;
    private String annotationHeadVar;
    private Query query;
    private int graphLevel = 0;

    public abstract void augmentBlock(TripleCollector arg0,
            String annotationBodyVar, String annotationHeadVar);

    public void augmentQuery(Query query, String annotationBodyVar, String annotationHeadVar) {
        this.annotationBodyVar = annotationBodyVar;
        this.annotationHeadVar = annotationHeadVar;
        this.query = query;
        query.getQueryPattern().visit(this);
    }

    public final String getAnnotationBodyVar() { return annotationBodyVar; }
    public final String getAnnotationHeadVar() { return annotationHeadVar; }
    public final Query getQuery() { return query; }
    public final boolean inDefaultGraph() { return (graphLevel == 0); }

    private void visitList(List<Element> elements) {
        for (Element e: elements) {
            System.err.println("Visit: " + e.getClass());
            e.visit(this);
        }
    }
    
    public void visit(ElementTriplesBlock arg0) {
        if (!inDefaultGraph()) augmentBlock(arg0, getAnnotationBodyVar(), getAnnotationHeadVar());
    }

    public void visit(ElementFilter arg0) {}

    public void visit(ElementAssign arg0) {}

    public void visit(ElementUnion arg0) {
        visitList(arg0.getElements());
    }

    public void visit(ElementOptional arg0) {
        arg0.getOptionalElement().visit(this);
    }

    public void visit(ElementGroup arg0) {
        visitList(arg0.getElements());
    }

    public void visit(ElementDataset arg0) {
        arg0.visit(this);
    }

    public void visit(ElementNamedGraph arg0) {
        System.err.println("Named graph! " + arg0.getElement().getClass());
        graphLevel++;
        arg0.getElement().visit(this);
        graphLevel--;
    }

    public void visit(ElementService arg0) {
        arg0.getElement().visit(this);
    }

    public void visit(ElementSubQuery arg0) {
        arg0.getQuery().getQueryPattern().visit(this);
    }

    public void visit(ElementPathBlock arg0) {
        if (!inDefaultGraph()) augmentBlock(arg0, getAnnotationBodyVar(), getAnnotationHeadVar());
    }

	public void visit(ElementFetch arg0) {
		/* See above */
	}

    public void visit(ElementExists elementExists) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void visit(ElementNotExists elementNotExists) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
