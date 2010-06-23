/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.caboto.security.sparql;

import com.hp.hpl.jena.sparql.expr.ExprEvalException;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase2;
import com.hp.hpl.jena.sparql.util.Symbol;
import org.caboto.security.GateKeeper;
import org.caboto.security.GateKeeper.Permission;

/**
 *
 * @author pldms
 */
public class GateKeeperFilter extends FunctionBase2 {

    public final static Symbol GATEKEEPER = Symbol.create("org.caboto.security.GK");
    public final static Symbol USER = Symbol.create("org.caboto.security.USER");
    private final GateKeeper gatekeeper;
    private final Object user;

    public GateKeeperFilter() {
        super();
        gatekeeper = (GateKeeper) this.getContext().get(GATEKEEPER);
        user = this.getContext().get(USER);
    }

    @Override
    public NodeValue exec(NodeValue permissionVal, NodeValue graphVal) {
        if (!permissionVal.isString())
            throw new ExprEvalException("Permission must be a string: " +
                    permissionVal);
        if (!graphVal.asNode().isURI())
            throw new ExprEvalException("Graph must be a URI: " +
                    graphVal);
        Permission perm = Permission.valueOf(permissionVal.asString());
        String graph = graphVal.asNode().getURI();
        return NodeValue.makeBoolean(gatekeeper.userHasPermissionFor(user, perm, graph));
    }

}
