package org.caboto.rest.resources;

import com.sun.ws.rest.spi.resource.Singleton;
import org.codehaus.jettison.json.JSONException;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
@Path("/about/{id}")
@ProduceMime("application/json")
@ConsumeMime("application/json")
@Singleton
public class AboutResource {

    // ----------------------- JSON Methods -----------------------

    @GET
    public Response getAnnotations(@PathParam("id")String id) throws JSONException {

        return Response.status(405).build();
    }

    @Context
    UriInfo uriInfo;
}