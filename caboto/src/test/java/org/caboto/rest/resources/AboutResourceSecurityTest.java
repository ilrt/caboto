/*
 * Copyright (c) 2008, University of Bristol
 * Copyright (c) 2008, University of Manchester
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the names of the University of Bristol and the
 *    University of Manchester nor the names of their
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
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

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
