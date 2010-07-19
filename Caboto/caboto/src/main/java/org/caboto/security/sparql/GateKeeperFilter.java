/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.caboto.security.sparql;

import com.hp.hpl.jena.sparql.expr.ExprEvalException;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase1;
import com.hp.hpl.jena.sparql.util.Symbol;
import org.caboto.security.GateKeeper;
import org.caboto.security.GateKeeper.Permission;

/**
 *
 * @author pldms
 */
public class GateKeeperFilter extends FunctionBase1 {

    public final static Symbol GATEKEEPER = Symbol.create("org.caboto.security.GK");
    public final static Symbol USER = Symbol.create("org.caboto.security.USER");
    
    public GateKeeperFilter() {
        super();
    }

    @Override
    public NodeValue exec(NodeValue graphVal) {
        GateKeeper gatekeeper = (GateKeeper) this.getContext().get(GATEKEEPER);
        Object user = this.getContext().get(USER);
        if (!graphVal.asNode().isURI())
            throw new ExprEvalException("Graph must be a URI: " +
                    graphVal);
        String graph = graphVal.asNode().getURI();
        return NodeValue.makeBoolean(gatekeeper.userHasPermissionFor(user, Permission.READ, graph));
    }

}
