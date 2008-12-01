/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.caboto.filters;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementAssign;
import com.hp.hpl.jena.sparql.syntax.ElementDataset;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementNamedGraph;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementService;
import com.hp.hpl.jena.sparql.syntax.ElementSubQuery;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.sparql.syntax.ElementUnsaid;
import com.hp.hpl.jena.sparql.syntax.ElementVisitor;
import java.util.List;

/**
 *
 * @author pldms
 */
public abstract class AnnotationFilterBase
        implements AnnotationFilter, ElementVisitor {

    private String annotationBodyVar;
    private Query query;
    private int graphLevel = 0;

    public abstract void augmentBlock(ElementTriplesBlock arg0,
            String annotationBodyVar);

    public void augmentQuery(Query query, String annotationBodyVar) {
        this.annotationBodyVar = annotationBodyVar;
        this.query = query;
        query.getQueryPattern().visit(this);
    }

    public final String getAnnotationBodyVar() { return annotationBodyVar; }
    public final Query getQuery() { return query; }
    public final boolean inDefaultGraph() { return (graphLevel == 0); }

    private void visitList(List<Element> elements) {
        for (Element e: elements) {
            e.visit(this);
        }
    }

    public void visit(ElementTriplesBlock arg0) {
        if (!inDefaultGraph()) augmentBlock(arg0, getAnnotationBodyVar());
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
        graphLevel++;
        arg0.getElement().visit(this);
        graphLevel--;
    }

    public void visit(ElementUnsaid arg0) {
        arg0.getElement().visit(this);
    }

    public void visit(ElementService arg0) {
        arg0.getElement().visit(this);
    }

    public void visit(ElementSubQuery arg0) {
        arg0.getQuery().getQueryPattern().visit(this);
    }
    public void visit(ElementPathBlock arg0) {
        /* Got me there! What's in this? */
    }
}
