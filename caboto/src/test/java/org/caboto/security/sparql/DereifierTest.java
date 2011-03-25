/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.caboto.security.sparql;

import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pldms
 */
public class DereifierTest {


    @Test
    public void testTransform() {
        Op toTransform = Algebra.parse(
                  "(bgp "
                + "(triple <a> <p> ??0) "
                + "(triple ??0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <c>) "
                + "(triple ??0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> <http://www.w3.org/1999/02/22-rdf-syntax-ns#nil>) "
                + ")"
        );
        Op got = Dereifier.apply(toTransform);
        Op expected = Algebra.parse(
                  "(bgp "
                + "(triple ?vanno1 <http://www.w3.org/2000/10/annotation-ns#annotates> <a>) "
                + "(triple ?vanno1 <http://www.w3.org/2000/10/annotation-ns#body> ?vbody1) "
                + "(triple ?vbody1 <p> ??0) "
                + "(triple ??0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <c>) "
                + "(triple ??0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> <http://www.w3.org/1999/02/22-rdf-syntax-ns#nil>) "
                + ")"
        );
        assertEquals(expected, got);
    }

    @Test
    public void testApply() {
    }

}