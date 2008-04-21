package org.caboto.rest.resources;

import com.sun.ws.rest.spi.resource.Singleton;
import org.codehaus.jettison.json.JSONException;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * <p>Class details</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
@Path("/sparql")
@ProduceMime("application/sparql-results+xml")
@ConsumeMime("application/sparql-query")
@Singleton
public class SparqlResource {

    // ----------------------- JSON Methods -----------------------

    @POST
    public Response query(String query) throws JSONException {

        return Response.status(405).build();
    }

    @Context
    UriInfo uriInfo;
}