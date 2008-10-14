package org.caboto.rest.resources;

import org.springframework.context.annotation.Scope;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Properties;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
@Scope("singleton")
@Path("/version/")
@Produces(MediaType.TEXT_PLAIN)
public class VersionResource {

    public VersionResource() {

        try {
            Properties properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream("version.properties"));
            version = properties.getProperty("caboto.version", "unknown");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @GET
    public Response getVersion() {

        return Response.ok().entity(version + "\n").build();
    }

    private String version;
}
