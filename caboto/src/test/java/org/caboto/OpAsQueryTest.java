/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.caboto;

import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.algebra.Op;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pldms
 */
public class OpAsQueryTest {

    /**
     * Test of asQuery method, of class OpAsQuery.
     */
    @Test
    public void testCountStar() {
        Object[] result = checkQuery("select count(*) { ?s ?p ?o }");
        assertEquals(result[0], result[1]);
    }
    
    @Test
    public void testCountGroup() {
        Object[] result = checkQuery("select count(?p) { ?s ?p ?o } group by ?s");
        assertEquals(result[0], result[1]);
    }
    
    @Test
    public void testDoubleCount() {
        Object[] result = checkQuery("select (count(?s) as ?sc) ?o (count(?p) as ?pc) { ?s ?p ?o }");
        assertEquals(result[0], result[1]);
    }
    
    public Object[] checkQuery(String query) {
        Query orig = QueryFactory.create(query, Syntax.syntaxARQ);
        Op a = Algebra.compile(orig);
        Query got = OpAsQuery.asQuery(a);
        //Object[] r = { orig, got };
        Object[] r = { a, Algebra.compile(got) };
        return r;
    }
}