/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.caboto.security.sparql;

import com.hp.hpl.jena.sparql.function.Function;
import com.hp.hpl.jena.sparql.function.FunctionFactory;

/**
 *
 * @author pldms
 */
public class GateKeeperFilterFactory implements FunctionFactory {

    public static final String GKURI = "http://caboto.org/functions#gatekeeper";

    public Function create(String uri) {
        return new GateKeeperFilter();
    }

}
