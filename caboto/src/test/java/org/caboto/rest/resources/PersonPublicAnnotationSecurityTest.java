package org.caboto.rest.resources;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.caboto.profile.ProfileRepositoryException;

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

        ClientResponse clientResponse = createPostClientResponse(null, null, userPublicUriOne,
                MediaType.APPLICATION_FORM_URLENCODED, validPostData);

        assertEquals("A 401 response should be returned", Response.Status.UNAUTHORIZED
                .getStatusCode(), clientResponse.getStatus());
    }


    @Test
    public void testAddAnnotationAuthenticated() {

        ClientResponse clientResponse = createPostClientResponse(usernameOne, passwordOne,
                userPublicUriOne, MediaType.APPLICATION_FORM_URLENCODED, validPostData);

        assertEquals("A 201 response should be returned", Response.Status.CREATED
                .getStatusCode(), clientResponse.getStatus());
    }

    @Test
    public void testAddAnnotationAuthenticatedIncorrectUserPath() {

        ClientResponse clientResponse = createPostClientResponse(usernameOne, passwordOne,
                userPublicUriTwo, MediaType.APPLICATION_FORM_URLENCODED, validPostData);

        assertEquals("A 403 response should be returned", Response.Status.FORBIDDEN
                .getStatusCode(), clientResponse.getStatus());
    }

    @Test
    public void testGetAnnotationUnauthenticated() throws ProfileRepositoryException {

        String url = createAndSaveAnnotation(userPublicUriOne);

        ClientResponse clientResponse = createGetClientResponse(null, null, url,
                MediaType.APPLICATION_JSON);

        assertEquals("A 200 should be returned", Response.Status.OK.getStatusCode(),
                clientResponse.getStatus());
    }

    @Test
    public void testGetAnnotationAuthenticated() throws ProfileRepositoryException {

        String url = createAndSaveAnnotation(userPublicUriOne);

        ClientResponse clientResponse = createGetClientResponse(usernameOne, passwordOne,
                url, MediaType.APPLICATION_JSON);

        assertEquals("A 200 should be returned", Response.Status.OK.getStatusCode(),
                clientResponse.getStatus());
    }

    @Test
    public void testDeleteAnnotationUnauthenticated() throws ProfileRepositoryException {

        // create an annotation to delete
        String url = createAndSaveAnnotation(userPublicUriOne);

        // check that the thing we want to delete actually exists
        ClientResponse clientResponse1 =
                createGetClientResponse(null, null, url, MediaType.APPLICATION_JSON);
        assertEquals("The resource sould return a 200", Response.Status.OK.getStatusCode(),
                clientResponse1.getStatus());

        // delete the resource
        Client c = Client.create();
        ClientResponse deleteResponse = c.resource(url).delete(ClientResponse.class);
        assertEquals("A 401 should be returned", Response.Status.UNAUTHORIZED.getStatusCode(),
                deleteResponse.getStatus());


    }

    @Test
    public void testDeleteAnnotationAuthenticated() throws ProfileRepositoryException {

        // create an annotation to delete
        String url = createAndSaveAnnotation(userPublicUriOne);

        // check that the thing we want to delete actually exists
        ClientResponse clientResponse1 =
                createGetClientResponse(usernameOne, passwordOne, url, MediaType.APPLICATION_JSON);
        assertEquals("The resource sould return a 200", Response.Status.OK.getStatusCode(),
                clientResponse1.getStatus());

        // delete the resource
        Client c = Client.create();
        c.addFilter(new BasicAuthenticationClientFilter(usernameOne, passwordOne));
        ClientResponse deleteResponse = c.resource(url).delete(ClientResponse.class);
        assertEquals("A 200 should be returned", Response.Status.OK.getStatusCode(),
                deleteResponse.getStatus());

        // make sure its not found
        ClientResponse clientResponse2 = createGetClientResponse(null, null, url,
                MediaType.APPLICATION_JSON);
        assertEquals("A 404 should be returned", Response.Status.NOT_FOUND.getStatusCode(),
                clientResponse2.getStatus());

    }

}


