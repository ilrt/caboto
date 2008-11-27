/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.caboto.rest.resources;

import com.hp.hpl.jena.rdf.model.Model;
import com.sun.jersey.api.client.ClientResponse;
import javax.ws.rs.core.Response;
import org.caboto.RdfMediaType;
import org.caboto.domain.Annotation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pldms
 */
public class AboutResourceTest extends AbstractResourceTest {
    @Before
    @Override
    public void setUp() {
        formatDataStore();
        startJettyWithSecurity();
    }

    @After
    @Override
    public void tearDown() {
        stopJetty();
    }

    /**
     * Test of findAnnotations method, of class AboutResource.
     */
    @Test
    public void testFindAnnotations() throws Exception {
        createAndSaveAnnotation(userPublicUriOne);
        String url = createAndSaveAnnotation(userPublicUriOne);
        ClientResponse response = createGetClientResponse(null, null,
                baseUri + "about/?rdf:type=foo",
                RdfMediaType.APPLICATION_RDF_XML);
        assertEquals("Got nothing", Response.Status.NOT_FOUND.getStatusCode(),
                response.getStatus());
        response = createGetClientResponse(null, null,
                url,
                RdfMediaType.APPLICATION_RDF_XML);
        response = createGetClientResponse(null, null,
                baseUri + "about/?dc:title=A%20title",
                RdfMediaType.APPLICATION_RDF_XML);
        assertEquals("Got something", Response.Status.OK.getStatusCode(),
                response.getStatus());
    }

}