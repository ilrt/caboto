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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
    public void testCount() {
        Object[] result = checkQuery("select count(*) { ?s ?p ?o }");
        assertEquals(result[0], result[1]);
    }
    
    public Object[] checkQuery(String query) {
        Query orig = QueryFactory.create(query, Syntax.syntaxARQ);
        Op a = Algebra.compile(orig);
        System.err.println(a);
        Query got = OpAsQuery.asQuery(a);
        Object[] r = { orig, got };
        return r;
    }
}