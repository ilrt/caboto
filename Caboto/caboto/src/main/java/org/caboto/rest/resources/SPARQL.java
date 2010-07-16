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
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.caboto.jena.db.Database;
import org.caboto.jena.db.Results;
import org.caboto.security.GateKeeper;
import org.caboto.security.sparql.GateKeeperEnforcer;
import org.caboto.security.sparql.GateKeeperFilter;
import org.caboto.security.sparql.GateKeeperFilterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

/**
 * REST Web Service
 *
 * @author pldms
 */

@Path("/query")
public class SPARQL {
    @Context
    private UriInfo context;

    @Autowired
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
    public String getXml(@QueryParam("query") String queryString) {
        Query query = QueryFactory.create(queryString);
        Op opQuery = Algebra.compile(query);
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

        if (query.isSelectType()) {
            Results result = database.executeSelectQuery(enforcedQuery.toString(), null);
            rep = ResultSetFormatter.asXMLString(result.getResults());
            result.close();
        } else if (query.isConstructType()) {
            Model result = database.executeConstructQuery(query, null);
            StringWriter sw = new StringWriter();
            result.write(sw, "RDF/XML-ABBREV");
            rep = sw.toString();
            result.close();
        }

        return rep;
    }
}
