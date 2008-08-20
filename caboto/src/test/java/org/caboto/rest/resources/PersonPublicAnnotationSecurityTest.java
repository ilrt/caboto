package org.caboto.rest.resources;

import com.sun.jersey.api.client.ClientResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        clearCredentials(); // in case they had been set in the test
        stopJetty();
    }


    @Test
    public void testAddAnnotationUnauthenticated() {

        ClientResponse clientResponse = createPostClientResponse(userUriOne,
                MediaType.APPLICATION_FORM_URLENCODED, validPostData);

        assertEquals("A 401 response should be returned", Response.Status.UNAUTHORIZED
                .getStatusCode(), clientResponse.getStatus());
    }


    @Test
    public void testAddAnnotationAuthenticated() {

        setCredentials(usernameOne, passwordOne);

        ClientResponse clientResponse = createPostClientResponse(userUriOne,
                MediaType.APPLICATION_FORM_URLENCODED, validPostData);

        assertEquals("A 201 response should be returned", Response.Status.CREATED
                .getStatusCode(), clientResponse.getStatus());
    }

    @Test
    public void testAddAnnotationAuthenticatedIncorrectUserPath() {

        setCredentials(usernameOne, passwordOne);

        ClientResponse clientResponse = createPostClientResponse(userUriTwo,
                MediaType.APPLICATION_FORM_URLENCODED, validPostData);

        assertEquals("A 403 response should be returned", Response.Status.FORBIDDEN
                .getStatusCode(), clientResponse.getStatus());
    }


}
