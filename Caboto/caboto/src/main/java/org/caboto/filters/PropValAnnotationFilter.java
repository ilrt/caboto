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

package org.caboto.filters;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementMinus;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.vocabulary.XSD;

/**
 * Prime annoyance: namespaces. what to do about them?
 *
 * @author pldms
 */
public class PropValAnnotationFilter extends AnnotationFilterBase {

    public final String propertyS;
    public final Node value;

    /**
     * 
     * @param propertyS
     * @param valueS
     */
    public PropValAnnotationFilter(final String propertyS,
            final String valueS) {
        this(propertyS, toValue(valueS));
    }

    public PropValAnnotationFilter(String propertyS, Node value) {
        this.propertyS = propertyS;
        this.value = value;
    }
    
    private static Node toValue(String valueS) {
        // Do we want number support?
        if (valueS.startsWith("U:"))
            return Node.createURI(valueS.substring(2));
        //return Node.createLiteral(valueS);
        return Node.createLiteral(valueS, null, XSDDatatype.XSDstring);
    }

    public void augmentBlock(ElementTriplesBlock arg0,
            String annotationBodyVar) {
        String expandedProp = getQuery().expandPrefixedName(propertyS);
        // TODO Caboto exception policy?
        if (expandedProp == null)
            throw new RuntimeException("Cannot expand property: " + propertyS);
        arg0.addTriple(Triple.create(
                Var.alloc(annotationBodyVar),
                Node.createURI(expandedProp),
                value));
    }

    public void visit(ElementMinus em) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
