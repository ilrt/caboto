/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.caboto.security.sparql;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.TransformBase;
import com.hp.hpl.jena.sparql.algebra.Transformer;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import org.caboto.vocabulary.Annotea;

/**
 * Take s p o -> ?anno ann:annotates ?s ; ann:body ?p ?o .
 * @author pldms
 */
public class Dereifier extends TransformBase {

    final static Dereifier instance = new Dereifier();
    final static Node ANNOTATES = Annotea.annotates.asNode();
    final static Node BODY = Annotea.body.asNode();

    // Take s p o -> ?anno ann:annotates ?s ; ann:body ?body . ?body ?p ?o .

    @Override
    public Op transform(OpBGP bgp) {
        BasicPattern pattern = bgp.getPattern();
        BasicPattern derei = new BasicPattern();

        int var = 0;

        for (Triple t: pattern.getList()) {
            var++;
            Var v = Var.alloc("vanno" + var);
            Var b = Var.alloc("vbody" + var);

            derei.add(Triple.create(v, ANNOTATES, t.getSubject()));
            derei.add(Triple.create(v, BODY, b));
            derei.add(Triple.create(b, t.getPredicate(), t.getObject()));
        }
        

        return new OpBGP(derei);
    }

    /**
     * Convenience method
     * Apply dereifier to a query (op)
     * @param op The query
     * @return dereified query
     */
    public static Op apply(Op op) {
        return Transformer.transform(instance, op);
    }
}
