/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.caboto.security.sparql;

import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.TransformBase;
import com.hp.hpl.jena.sparql.algebra.Transformer;
import com.hp.hpl.jena.sparql.algebra.op.OpFilter;
import com.hp.hpl.jena.sparql.algebra.op.OpGraph;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_Function;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.NodeValue;

/**
 *
 * @author pldms
 */
public class GateKeeperEnforcer extends TransformBase {

    final static GateKeeperEnforcer instance = new GateKeeperEnforcer();

    @Override
    public Op transform(OpGraph og, Op body) {

        // graph g { ... } ->
        // graph g { ... } filter ( <GKURI>(g) )

        Expr arg = (og.getNode() instanceof Var) ?
            new ExprVar(og.getNode()) :
            NodeValue.makeNode(og.getNode()) ;
        return OpFilter.filter(
                new E_Function(GateKeeperFilterFactory.GKURI, new ExprList(arg)),
                og
                );
    }

    /**
     * Convenience method
     * Apply gatekeeper graph enforcement to a query (op)
     * @param op The query
     * @return Enforced query
     */
    public static Op apply(Op op) {
        return Transformer.transform(instance, op);
    }
}
