/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.caboto.security.sparql;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.TransformCopy;
import com.hp.hpl.jena.sparql.algebra.Transformer;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.vocabulary.RDF;
import org.caboto.vocabulary.Annotea;

/**
 * Take s p o -> ?anno ann:annotates ?s ; ann:body ?p ?o .
 * 
 * Includes model dereifier
 * 
 * @author pldms
 */
public class Dereifier extends TransformCopy {

    final static Node ANNOTATES = Annotea.annotates.asNode();
    final static Node BODY = Annotea.body.asNode();

    int varnum = 0;

    // Take s p o -> ?anno ann:annotates ?s ; ann:body ?body . ?body ?p ?o .

    @Override
    public Op transform(OpBGP bgp) {
        BasicPattern pattern = bgp.getPattern();
        BasicPattern derei = new BasicPattern();

        for (Triple t: pattern.getList()) {
            // Don't mess with collections
            if (RDF.Nodes.first.equals(t.getPredicate()) ||
                RDF.Nodes.rest.equals(t.getPredicate())) {
                derei.add(t);
                continue;
            }
            varnum++;
            Var v = Var.alloc("vanno" + varnum);
            Var b = Var.alloc("vbody" + varnum);

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
        return Transformer.transform(new Dereifier(), op);
    }
    
    // This moves the annotated thing to the body position
    final static Query derei = QueryFactory.create(
      "prefix anno: <http://www.w3.org/2000/10/annotation-ns#> "
            + "construct { ?s ?p ?o } "
            + "{ ?head anno:body ?body ; anno:annotates ?s . ?body ?p ?o . }"
            ); 
    
    /**
     * Dereify a model
     * @param model
     * @return stripped model
     */
    public static Model dereify(Model model) {
        QueryExecution qe = QueryExecutionFactory.create(derei, model);
        Model result = qe.execConstruct();
        qe.close();
        return result;
    }
}
