package org.caboto.rest.resources;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 */
public class PersonPublicAnnotationTest extends AbstractResourceTest {

    @Test
    public void testAddPublicAnnotationWithGarbage() {

        WebResource webResource = createWebResource(baseUri + userPublicUri);

        ClientResponse clientResponse = createClientResponse(webResource, garbagePostData);

        assertEquals("A 400 response should be returned", 400, clientResponse.getStatus());

    }

    @Test
    public void testAddPublicAnnotation() {

        WebResource webResource = createWebResource(baseUri + userPublicUri);

        ClientResponse clientResponse = createClientResponse(webResource, validPostData);

        assertEquals("A 201 response should be returned", 201, clientResponse.getStatus());
        assertTrue("The created location should start with " + baseUri + userPublicUri,
                clientResponse.getLocation().toString().startsWith(baseUri + userPublicUri));

    }


    private WebResource createWebResource(String uri) {
        Client c = Client.create();
        return c.resource(uri);
    }

    private ClientResponse createClientResponse(WebResource webResource, String postData) {
        return webResource.type(MediaType.APPLICATION_FORM_URLENCODED)
                .post(ClientResponse.class, postData);
    }

    private String baseUri = "http://localhost:9090/caboto/person/";

    private String userPublicUri = "mike/public/";

    private String validPostData = "title=A%20Title&description=A%20description&type=" +
            "SimpleComment&annotates=http%3A%2F%2Fexample.org%2Fthing";

    private String garbagePostData = "aaabbbcccdddeeefffggghhhiii";
}
