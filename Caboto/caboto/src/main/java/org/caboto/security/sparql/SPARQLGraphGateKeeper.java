/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.caboto.security.sparql;

import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.TransformBase;
import com.hp.hpl.jena.sparql.algebra.op.OpFilter;
import com.hp.hpl.jena.sparql.algebra.op.OpGraph;
import com.hp.hpl.jena.sparql.expr.E_Function;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.NodeValue;

/**
 *
 * @author pldms
 */
public class SPARQLGraphGateKeeper extends TransformBase {

    /**
     * Apply a filter ensuring that user has permission to read
     * graph og.getNode().
     * @param og
     * @param subop
     * @return
     */
    @Override
    public Op transform(OpGraph og, Op subop) {
        // <GKURI>(graph_node)
        Expr expr = new E_Function(GateKeeperFilterFactory.GKURI,
                new ExprList(NodeValue.makeNode(og.getNode())));
        return OpFilter.filter(new ExprList(expr), og);
    }

}
