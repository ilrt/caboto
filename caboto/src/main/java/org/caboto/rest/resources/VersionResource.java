package org.caboto.rest.resources;

import com.sun.jersey.spi.inject.Inject;
import com.sun.jersey.spi.resource.PerRequest;
import org.caboto.domain.VersionDetails;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
@PerRequest
@Path("/version/")
@Produces(MediaType.TEXT_PLAIN)
public class VersionResource {

    @GET
    public Response getVersion() {

        return Response.ok().entity(versionDetails.getCabotoVersion()).build();
    }

    @Inject
    private VersionDetails versionDetails = null;
}
