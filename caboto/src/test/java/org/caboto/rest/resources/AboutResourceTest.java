package org.caboto.rest.resources;

import com.hp.hpl.jena.rdf.model.Model;
import com.sun.jersey.api.client.ClientResponse;
import org.caboto.RdfMediaType;
import org.caboto.profile.ProfileRepositoryException;
import org.junit.Before;
import org.junit.Test;
import org.codehaus.jettison.json.JSONArray;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class AboutResourceTest extends AbstractResourceTest {

    @Before
    public void setUp() {
        super.setUp();

        requestUri = baseUri + "about/?id=" + annotated;

        try {
            annotationUrlOne = createAndSaveAnnotation();
            annotationUrlTwo = createAndSaveAnnotation();
        } catch (ProfileRepositoryException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindAnnotationsAsJson() {

        ClientResponse clientResponse = createGetClientResponse(requestUri,
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

        ClientResponse clientResponse = createGetClientResponse(requestUri,
                RdfMediaType.TEXT_RDF_N3);
        
        assertEquals("A 200 response should be returned", Response.Status.OK.getStatusCode(),
                clientResponse.getStatus());

        assertEquals("Wrong type returned", RdfMediaType.TEXT_RDF_N3_TYPE,
                clientResponse.getType());

        Model model = clientResponse.getEntity(Model.class);

        assertTrue("Resource one not found",
                model.containsResource(com.hp.hpl.jena.rdf.model.ResourceFactory
                        .createResource(this.annotationUrlOne)));

        assertTrue("Resource two not found",
                model.containsResource(com.hp.hpl.jena.rdf.model.ResourceFactory
                        .createResource(this.annotationUrlTwo)));

    }

    @Test
    public void testFindAnnotationsAsRdfXml() {

        ClientResponse clientResponse = createGetClientResponse(requestUri,
                RdfMediaType.APPLICATION_RDF_XML);

        assertEquals("A 200 response should be returned", Response.Status.OK.getStatusCode(),
                clientResponse.getStatus());

        assertEquals("Wrong type returned", RdfMediaType.APPLICATION_RDF_XML_TYPE,
                clientResponse.getType());

        Model model = clientResponse.getEntity(Model.class);

        assertTrue("Resource one not found",
                model.containsResource(com.hp.hpl.jena.rdf.model.ResourceFactory
                        .createResource(this.annotationUrlOne)));

        assertTrue("Resource two not found",
                model.containsResource(com.hp.hpl.jena.rdf.model.ResourceFactory
                        .createResource(this.annotationUrlTwo)));

    }

    @Test
    public void testGetMissingResource() {

        ClientResponse clientResponse =
                createGetClientResponse(requestUri + "aresourcethatdoesntexist",
                        MediaType.APPLICATION_JSON);

        assertEquals("A 404 response should be returned", Response.Status.NOT_FOUND
                .getStatusCode(), clientResponse.getStatus());

    }
    private String annotationUrlOne;
    private String annotationUrlTwo;
    private String requestUri;

}
