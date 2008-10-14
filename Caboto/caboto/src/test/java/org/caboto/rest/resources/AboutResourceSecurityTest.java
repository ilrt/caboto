package org.caboto.rest.resources;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.sun.jersey.api.client.ClientResponse;
import org.caboto.RdfMediaType;
import org.caboto.profile.ProfileRepositoryException;
import org.codehaus.jettison.json.JSONArray;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
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

    @Test
    public void testFindAnnotationsAsJson() {

        ClientResponse clientResponse = createGetClientResponse(null, null, requestUri,
                MediaType.APPLICATION_JSON);

        assertEquals("A 200 response should be returned", Response.Status.OK.getStatusCode(),
                clientResponse.getStatus());

        assertEquals("Wrong type returned", MediaType.APPLICATION_JSON_TYPE,
                clientResponse.getType());

        JSONArray results = clientResponse.getEntity(JSONArray.class);

        assertEquals("There should be two results", 2, results.length());

        // best way to check for ids in existing???

    }

    @Test
    public void testFindAnnotationsAsRdfN3() {

        ClientResponse clientResponse = createGetClientResponse(null, null, requestUri,
                RdfMediaType.TEXT_RDF_N3);

        assertEquals("A 200 response should be returned", Response.Status.OK.getStatusCode(),
                clientResponse.getStatus());

        assertEquals("Wrong type returned", RdfMediaType.TEXT_RDF_N3_TYPE,
                clientResponse.getType());

        Model model = clientResponse.getEntity(Model.class);

        assertTrue("Resource one not found",
                model.containsResource(com.hp.hpl.jena.rdf.model.ResourceFactory
                        .createResource(publicAnnotationUrlOne)));

        assertTrue("Resource two not found",
                model.containsResource(com.hp.hpl.jena.rdf.model.ResourceFactory
                        .createResource(publicAnnotationUrlTwo)));

    }

    @Test
    public void testFindAnnotationsAsRdfXml() {

        ClientResponse clientResponse = createGetClientResponse(null, null, requestUri,
                RdfMediaType.APPLICATION_RDF_XML);

        assertEquals("A 200 response should be returned", Response.Status.OK.getStatusCode(),
                clientResponse.getStatus());

        assertEquals("Wrong type returned", RdfMediaType.APPLICATION_RDF_XML_TYPE,
                clientResponse.getType());

        Model model = clientResponse.getEntity(Model.class);

        assertTrue("Resource one not found",
                model.containsResource(com.hp.hpl.jena.rdf.model.ResourceFactory
                        .createResource(publicAnnotationUrlOne)));

        assertTrue("Resource two not found",
                model.containsResource(com.hp.hpl.jena.rdf.model.ResourceFactory
                        .createResource(publicAnnotationUrlTwo)));

    }

    @Test
    public void testGetMissingResource() {

        ClientResponse clientResponse =
                createGetClientResponse(null, null, requestUri + "aresourcethatdoesntexist",
                        MediaType.APPLICATION_JSON);

        assertEquals("A 404 response should be returned", Response.Status.NOT_FOUND
                .getStatusCode(), clientResponse.getStatus());

    }

    private String requestUri;
    private String publicAnnotationUrlOne;
    private String publicAnnotationUrlTwo;
    private String privateAnnotationUrlOne;
}
