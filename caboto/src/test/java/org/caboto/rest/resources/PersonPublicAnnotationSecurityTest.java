package org.caboto.rest.resources;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import com.sun.jersey.api.client.ClientResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class PersonPublicAnnotationSecurityTest extends AbstractResourceTest {

    @Before
    public void setUp() {
        formatDataStore();
        startJettyWithSecurity();
    }

    @After
    public void tearDown() {
        stopJetty();
    }


    @Test
    public void testAddAnnotationUnauthenticated() {

        ClientResponse clientResponse = createPostClientResponse(userPublicUriUnauthenticated,
                MediaType.APPLICATION_FORM_URLENCODED, validPostData);

        assertEquals("A 401 response should be returned", Response.Status.UNAUTHORIZED
                .getStatusCode(), clientResponse.getStatus());
    }

    /**
    @Test
    public void testAddAnnotationAuthenticated() {

        ClientResponse clientResponse = createPostClientResponse(userPublicUriAuthenticated,
                MediaType.APPLICATION_FORM_URLENCODED, validPostData);

        assertEquals("A 201 response should be returned", Response.Status.CREATED
                .getStatusCode(), clientResponse.getStatus());
    }
    **/
}
