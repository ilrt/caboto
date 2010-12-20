/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.caboto.rest.resources;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.function.FunctionRegistry;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import org.caboto.OpAsQuery; // Use our own patched version
import org.caboto.jena.db.Database;
import org.caboto.security.GateKeeper;
import org.caboto.security.sparql.Dereifier;
import org.caboto.security.sparql.GateKeeperEnforcer;
import org.caboto.security.sparql.GateKeeperFilter;
import org.caboto.security.sparql.GateKeeperFilterFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * REST Web Service
 *
 * @author pldms
 */

@Path("/query/{type}")
@Component
@Scope("singleton")
public class SPARQL {

    //static final Logger log = LoggerFactory.getLogger(SPARQL.class);

    @Context
    private UriInfo context;

    @Autowired
    @Qualifier("database")
    private Database database = null;

    @Autowired
    private GateKeeper gatekeeper = null;

    /** Creates a new instance of SPARQL */
    public SPARQL() {
        // Register our function
        FunctionRegistry.get().put(GateKeeperFilterFactory.GKURI,
                new GateKeeperFilterFactory());
    }
    
    /**
     * Present caboto store as a sparql endpoint
     * 
     * @param type Raw or dereified
     * @param queryString
     * @return Model, Result or Boolean (depending on query form)
     */
    @GET
    public Object performQuery(@PathParam("type") QueryType type,
            @QueryParam("query") String queryString) {
        Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);
        Op opQuery = Algebra.compile(query);
        // Flatten annotations for this endpoint
        if (type == QueryType.relations) opQuery = Dereifier.apply(opQuery);

        Op enforcedOpQuery = GateKeeperEnforcer.apply(opQuery);
        Query enforcedQuery = OpAsQuery.asQuery(enforcedOpQuery);
        
        // Copy over type. Not part of algebra.
        if (query.isAskType()) enforcedQuery.setQueryAskType();
        else if (query.isConstructType()) enforcedQuery.setQueryConstructType();
        else if (query.isDescribeType()) enforcedQuery.setQueryDescribeType();
        // default is SELECT
               
        // Set up the context...
        database.setQueryContext(GateKeeperFilter.GATEKEEPER, gatekeeper);
        // THIS IS HORRIBLE. WHAT WAS SPRING THINKING?
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        database.setQueryContext(GateKeeperFilter.USER, auth);
                
        if (enforcedQuery.isSelectType()) {
            return database.executeSelectQuery(enforcedQuery, null);
        } else if (enforcedQuery.isConstructType()) {
            return database.executeConstructQuery(enforcedQuery, null);
        } else if (enforcedQuery.isDescribeType()) {
            return database.executeDescribeQuery(enforcedQuery, null);
        } else {// ASK
            return database.executeAskQuery(enforcedQuery, null);
        }        
    }

    public static enum QueryType { annotations, relations }
}
