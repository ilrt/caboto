/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.caboto.filters;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryVisitor;
import com.hp.hpl.jena.sparql.core.Prologue;

/**
 *
 * @author pldms
 */
public abstract class AnnotationFilterBase
        implements AnnotationFilter, QueryVisitor {

    private String annotationBodyVar;

    public void augmentQuery(Query query, String annotationBodyVar) {
        this.annotationBodyVar = annotationBodyVar;
        query.visit(this);
    }

    public final String getAnnotationBodyVar() { return annotationBodyVar; }

    public void startVisit(Query arg0) {}

    public void visitPrologue(Prologue arg0) {}

    public void visitResultForm(Query arg0) {}

    public void visitSelectResultForm(Query arg0) {}

    public void visitConstructResultForm(Query arg0) {}

    public void visitDescribeResultForm(Query arg0) {}

    public void visitAskResultForm(Query arg0) {}

    public void visitDatasetDecl(Query arg0) {}

    /* public void visitQueryPattern(Query arg0) {} */

    public void visitGroupBy(Query arg0) {}

    public void visitHaving(Query arg0) {}

    public void visitOrderBy(Query arg0) {}

    public void visitLimit(Query arg0) {}

    public void visitOffset(Query arg0) {}

    public void finishVisit(Query arg0) {}

}
