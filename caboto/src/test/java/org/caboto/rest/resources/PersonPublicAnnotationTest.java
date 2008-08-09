package org.caboto.rest.resources;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.caboto.RdfMediaType;
import org.caboto.dao.AnnotationDao;
import org.caboto.dao.AnnotationDaoImpl;
import org.caboto.domain.Annotation;
import org.caboto.profile.ProfileRepository;
import org.caboto.profile.ProfileRepositoryException;
import org.caboto.profile.ProfileRepositoryXmlImpl;
import org.caboto.rest.providers.JenaModelRdfProvider;
import org.caboto.rest.providers.JenaResourceRdfProvider;
import org.caboto.store.StoreFactory;
import org.caboto.store.StoreFactoryDefaultImpl;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 */
public class PersonPublicAnnotationTest extends AbstractResourceTest {

    @Before
    public void setUp() {

        super.setUp();

    }


    @Test
    public void testAddPublicAnnotationWithGarbage() {

        ClientResponse clientResponse = createPostClientResponse(baseUri + userPublicUri,
                MediaType.APPLICATION_FORM_URLENCODED, garbagePostData);

        assertEquals("A 400 response should be returned", 400, clientResponse.getStatus());

    }

    @Test
    public void testAddPublicAnnotation() {

        ClientResponse clientResponse = createPostClientResponse(baseUri + userPublicUri,
                MediaType.APPLICATION_FORM_URLENCODED, validPostData);

        assertEquals("A 201 response should be returned", 201, clientResponse.getStatus());
        assertTrue("The created location should start with " + baseUri + userPublicUri,
                clientResponse.getLocation().toString().startsWith(baseUri + userPublicUri));

    }


    @Test
    public void testGetPublicAnnotationAsJson() throws ProfileRepositoryException, JSONException {

        // the url of the resource
        String url = createAndSaveAnnotation();

        ClientResponse clientResponse =
                createGetClientResponse(url, MediaType.APPLICATION_JSON);

        JSONObject object = clientResponse.getEntity(JSONObject.class);

        assertEquals("A 200 response should be returned", 200, clientResponse.getStatus());

        assertEquals("The default type should be application/json", MediaType.APPLICATION_JSON_TYPE,
                clientResponse.getType());

        assertEquals("The IDs do not match", url, object.getString("id"));
    }


    @Test
    public void testGetPublicAnnotationAsRdfXml() throws ProfileRepositoryException, JSONException {

        // the url of the resource
        String url = createAndSaveAnnotation();

        ClientResponse clientResponse =
                createGetClientResponse(url, RdfMediaType.APPLICATION_RDF_XML);

        Model model = clientResponse.getEntity(Model.class);

        assertEquals("A 200 response should be returned", 200, clientResponse.getStatus());

        assertTrue("The URI is not in the model",
                model.containsResource(ResourceFactory.createResource(url)));

        assertEquals("The wrong media type", RdfMediaType.APPLICATION_RDF_XML_TYPE,
                clientResponse.getType());

    }

    @Test
    public void testGetPublicAnnotationAsRdfN3() throws ProfileRepositoryException, JSONException {

        // the url of the resource
        String url = createAndSaveAnnotation();

        ClientResponse clientResponse =
                createGetClientResponse(url, RdfMediaType.TEXT_RDF_N3);

        Model model = clientResponse.getEntity(Model.class);

        assertEquals("A 200 response should be returned", 200, clientResponse.getStatus());

        assertTrue("The URI is not in the model",
                model.containsResource(ResourceFactory.createResource(url)));

        assertEquals("The wrong media type", RdfMediaType.TEXT_RDF_N3_TYPE,
                clientResponse.getType());

    }

    private ClientResponse createPostClientResponse(String uri, String type, String postData) {

        Client c = Client.create();
        return c.resource(uri).type(type).post(ClientResponse.class, postData);
    }


    private ClientResponse createGetClientResponse(String uri, String type) {

        Client c = Client.create(createClientConfig());
        return c.resource(uri).accept(type).get(ClientResponse.class);
    }

    private ClientConfig createClientConfig() {

        ClientConfig config = new DefaultClientConfig();
        config.getProviderClasses().add(JenaResourceRdfProvider.class);
        config.getProviderClasses().add(JenaModelRdfProvider.class);

        return config;
    }

    private String createAndSaveAnnotation() throws ProfileRepositoryException {
        Annotation annotation = createTestAnnotation();
        saveAnnotation(annotation);
        return annotation.getId();
    }

    private Annotation createTestAnnotation() {

        // body of the annotation
        Map<String, String> body = new HashMap<String, String>();
        body.put("title", "A title");
        body.put("description", "A description");

        // main bits of the annotation
        Annotation annotation = new Annotation();
        annotation.setAnnotates("http://example.org/");
        annotation.setType("SimpleComment");
        annotation.setGraphId(baseUri + userPublicUri);
        annotation.setBody(body);

        return annotation;
    }

    private void saveAnnotation(Annotation annotation) throws ProfileRepositoryException {

        StoreFactory storeFactory = new StoreFactoryDefaultImpl("/sdb.ttl");

        ProfileRepository profileRepository = new ProfileRepositoryXmlImpl("test-profiles.xml");
        AnnotationDao annotationDao = new AnnotationDaoImpl(profileRepository, storeFactory);

        annotationDao.addAnnotation(annotation);

    }

    private String baseUri = "http://localhost:9090/caboto/person/";

    private String userPublicUri = "mike/public/";

    private String validPostData = "title=A%20Title&description=A%20description&type=" +
            "SimpleComment&annotates=http%3A%2F%2Fexample.org%2Fthing";

    private String garbagePostData = "aaabbbcccdddeeefffggghhhiii";


}
