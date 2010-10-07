/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.caboto.rest.resources;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpAsQuery;
import com.hp.hpl.jena.sparql.function.FunctionRegistry;
import java.io.StringWriter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.caboto.jena.db.Database;
import org.caboto.jena.db.Results;
import org.caboto.security.GateKeeper;
import org.caboto.security.sparql.Dereifier;
import org.caboto.security.sparql.GateKeeperEnforcer;
import org.caboto.security.sparql.GateKeeperFilter;
import org.caboto.security.sparql.GateKeeperFilterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
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

    static final Logger log = LoggerFactory.getLogger(SPARQL.class);

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
     * Retrieves representation of an instance of org.caboto.resources.SPARQL
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/xml")
    public String getXml(@PathParam("type") QueryType type,
            @QueryParam("query") String queryString) {
        Query query = QueryFactory.create(queryString);
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

        String rep = null;

        if (enforcedQuery.isSelectType()) {
            Results result = database.executeSelectQuery(enforcedQuery.toString(), null);
            rep = ResultSetFormatter.asXMLString(result.getResults());
            result.close();
        } else if (enforcedQuery.isConstructType()) {
            Model result = database.executeConstructQuery(enforcedQuery, null);
            StringWriter sw = new StringWriter();
            result.write(sw, "RDF/XML-ABBREV");
            rep = sw.toString();
            result.close();
        }

        return rep;
    }

    public static enum QueryType { annotations, relations }
}
