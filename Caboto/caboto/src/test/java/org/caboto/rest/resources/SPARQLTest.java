/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.caboto.rest.resources;

import javax.ws.rs.core.Response;
import org.caboto.RdfMediaType;
import com.sun.jersey.api.client.ClientResponse;
import org.junit.After;
import org.caboto.profile.ProfileRepositoryException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pldms
 */
public class SPARQLTest extends AbstractResourceTest {

    private String requestUri;

    public SPARQLTest() {
    }

    @Before
    public void setUp() throws ProfileRepositoryException {
        formatDataStore();
        startJettyWithSecurity();

        requestUri = baseUri + "about/?id=" + annotated;

        String publicAnnotationUrlOne = createAndSaveAnnotation(userPublicUriOne);
        String publicAnnotationUrlTwo = createAndSaveAnnotation(userPublicUriOne);
        String privateAnnotationUrlOne = createAndSaveAnnotation(userPrivateUriOne);
    }

    @After
    public void tearDown() {
        stopJetty();
    }

    @Test
    public void testFindAnnotationsUnauthenticated() {

        ClientResponse clientResponse = createGetClientResponse(null, null, requestUri,
                RdfMediaType.APPLICATION_RDF_XML);

        assertEquals("A 200 response should be returned", Response.Status.OK.getStatusCode(),
                clientResponse.getStatus());


    }

    @Test
    public void testFindAnnotationsAuthenticated() {

        ClientResponse clientResponse = createGetClientResponse(usernameOne, passwordOne,
                requestUri, RdfMediaType.APPLICATION_RDF_XML);

        assertEquals("A 200 response should be returned", Response.Status.OK.getStatusCode(),
                clientResponse.getStatus());


    }
}
