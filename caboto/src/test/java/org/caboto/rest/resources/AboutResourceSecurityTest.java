package org.caboto.rest.resources;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.sun.jersey.api.client.ClientResponse;
import org.caboto.RdfMediaType;
import org.caboto.profile.ProfileRepositoryException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

public class AboutResourceSecurityTest extends AbstractResourceTest {


    @Before
    public void setUp() {
        formatDataStore();
        startJettyWithSecurity();

        requestUri = baseUri + "about/?id=" + annotated;

        try {
            publicAnnotationUrlOne = createAndSaveAnnotation(userPublicUriOne);
            publicAnnotationUrlTwo = createAndSaveAnnotation(userPublicUriOne);
            privateAnnotationUrlOne = createAndSaveAnnotation(userPrivateUriOne);
        } catch (ProfileRepositoryException e) {
            e.printStackTrace();
        }


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

        Model results = clientResponse.getEntity(Model.class);

        assertEquals("The model size should be 14", 14, results.size());
        assertTrue("Graph not found " + userPublicUriOne,
                results.containsResource(ResourceFactory.createResource(publicAnnotationUrlOne)));
        assertTrue("Graph not found " + userPublicUriOne,
                results.containsResource(ResourceFactory.createResource(publicAnnotationUrlTwo)));
        assertFalse("Graph found " + userPublicUriOne,
                results.containsResource(ResourceFactory.createResource(privateAnnotationUrlOne)));

    }

    @Test
    public void testFindAnnotationsAuthenticated() {

        ClientResponse clientResponse = createGetClientResponse(usernameOne, passwordOne,
                requestUri, RdfMediaType.APPLICATION_RDF_XML);

        assertEquals("A 200 response should be returned", Response.Status.OK.getStatusCode(),
                clientResponse.getStatus());

        Model results = clientResponse.getEntity(Model.class);

        assertEquals("The model size should be 21", 21, results.size());
        assertTrue("Graph not found " + userPublicUriOne,
                results.containsResource(ResourceFactory.createResource(publicAnnotationUrlOne)));
        assertTrue("Graph not found " + userPublicUriOne,
                results.containsResource(ResourceFactory.createResource(publicAnnotationUrlTwo)));
        assertTrue("Graph not found " + userPublicUriOne,
                results.containsResource(ResourceFactory.createResource(privateAnnotationUrlOne)));

    }


    private String requestUri;
    private String publicAnnotationUrlOne;
    private String publicAnnotationUrlTwo;
    private String privateAnnotationUrlOne;
}
