package org.caboto.rest.resources;

import com.sun.research.ws.wadl.Response;
import com.sun.jersey.spi.resource.Singleton;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ProduceMime;

/**
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 *
 **/
@Path("/person/{uid}/public/")
@Singleton
public class PersonPublicAnnotation {

    @GET
    @ProduceMime("text/plain")
    public String getPersonPublicAnnotation() {
        return "Hello\n";
    }

}
