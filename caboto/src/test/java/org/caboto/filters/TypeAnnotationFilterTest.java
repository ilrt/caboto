/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.caboto.filters;

import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Query;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pldms
 */
public class TypeAnnotationFilterTest {
    
    @Test 
    public void checkBasics() {
        TypeAnnotationFilter filter = new TypeAnnotationFilter("ex:Type");
        Query expected = QueryFactory.create("prefix ex: <urn:x:> select * { graph ?g { ?s ?p ?o ; a ex:Type } }");
        Query result = QueryFactory.create("prefix ex: <urn:x:> select * { graph ?g { ?s ?p ?o } }");
        filter.augmentQuery(result, "none", "s");
        assertEquals("Query augmented", Algebra.compile(expected), Algebra.compile(result));
    }
    
}