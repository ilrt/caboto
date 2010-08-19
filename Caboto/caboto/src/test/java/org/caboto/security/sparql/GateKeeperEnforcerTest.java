/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.caboto.security.sparql;

import com.hp.hpl.jena.sparql.algebra.Transformer;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pldms
 */
public class GateKeeperEnforcerTest {

    public GateKeeperEnforcerTest() {
    }

    /**
     * Test of transform method, of class GateKeeperEnforcer.
     */
    @Test
    public void testVarTransform() {
        Op orig = Algebra.parse("(graph ?g (bgp (triple ?s ?p ?o)))");
        Op expected = Algebra.parse("(filter (<http://caboto.org/functions#gatekeeper> ?g) (graph ?g (bgp (triple ?s ?p ?o))))");
        GateKeeperEnforcer instance = new GateKeeperEnforcer();
        Op result = Transformer.transform(instance, orig);
        assertEquals(expected, result);
    }

    @Test
    public void testFixedTransform() {
        Op orig = Algebra.parse("(graph <urn:ex:graph> (bgp (triple ?s ?p ?o)))");
        Op expected = Algebra.parse("(filter (<http://caboto.org/functions#gatekeeper> <urn:ex:graph>) (graph <urn:ex:graph> (bgp (triple ?s ?p ?o))))");
        GateKeeperEnforcer instance = new GateKeeperEnforcer();
        Op result = Transformer.transform(instance, orig);
        assertEquals(expected, result);
    }

}