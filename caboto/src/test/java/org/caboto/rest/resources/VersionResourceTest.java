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
public class VersionResourceTest extends AbstractResourceTest {

    @Before
    public void setUp() {
        formatDataStore();
        startJetty();
    }

    @After
    public void tearDown() {
        stopJetty();
    }

    @Test
    public void testVersion() {

        String url = baseUri + "version/";
        ClientResponse clientResponse = createGetClientResponse(null, null, url,
                MediaType.TEXT_PLAIN);

        assertEquals("Should return a 200", Response.Status.OK.getStatusCode(),
                clientResponse.getStatus());
        assertNotNull("Response entity should not be null", clientResponse.getEntity(String.class));

    }
}
