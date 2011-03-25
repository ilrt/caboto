/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.caboto.rest.resources;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

    private final static String QUERY_TYPE = "select * { graph ?g { ?s a ?o } }";
    private final static String QUERY_ALL = "select * { graph ?g { ?s ?p ?o } }";

    private String typeFullURI;
    private String allFullURI;
    private String allRelURI;

    public SPARQLTest() {
    }
    
    private final String makeQueryAnno(final String sparql) throws UnsupportedEncodingException {
        return baseUri + "query/annotations?query=" + URLEncoder.encode(sparql, "UTF-8");
    }
    
    @Before
    public void setUp() throws ProfileRepositoryException, UnsupportedEncodingException {
        formatDataStore();
        startJettyWithSecurity();

        typeFullURI = baseUri + "query/annotations?query=" + URLEncoder.encode(QUERY_TYPE, "UTF-8");
        allFullURI  = baseUri + "query/annotations?query=" + URLEncoder.encode(QUERY_ALL, "UTF-8");
        allRelURI  = baseUri + "query/relations?query=" + URLEncoder.encode(QUERY_ALL, "UTF-8");

        String publicAnnotationUrlOne = createAndSaveAnnotation(userPublicUriOne);
        String publicAnnotationUrlTwo = createAndSaveAnnotation(userPublicUriOne);
        String privateAnnotationUrlOne = createAndSaveAnnotation(userPrivateUriOne);
    }

    @After
    public void tearDown() {
        stopJetty();
    }

    @Test
    public void testQueryUnauthenticated() {

        ClientResponse clientResponse = createGetClientResponse(null, null, typeFullURI,
                RdfMediaType.APPLICATION_XML);

        assertEquals("A 200 response should be returned", Response.Status.OK.getStatusCode(),
                clientResponse.getStatus());

        ResultSet res =
                ResultSetFactory.fromXML(clientResponse.getEntityInputStream());

        ResultSetRewindable resrw = ResultSetFactory.makeRewindable(res);

        assertEquals(2, resrw.size()); // got the two public types
    }

    @Test
    public void testQueryAuthenticated() {

        ClientResponse clientResponse = createGetClientResponse(usernameOne, passwordOne,
                typeFullURI, RdfMediaType.APPLICATION_XML);

        assertEquals("A 200 response should be returned", Response.Status.OK.getStatusCode(),
                clientResponse.getStatus());

        ResultSet res =
                ResultSetFactory.fromXML(clientResponse.getEntityInputStream());

        ResultSetRewindable resrw = ResultSetFactory.makeRewindable(res);

        assertEquals(3, resrw.size()); // got the two public and one private type
    }

    @Test
    public void testDereiQueryAuthenticated() {

        ClientResponse clientResponse = createGetClientResponse(usernameOne, passwordOne,
                allRelURI, RdfMediaType.APPLICATION_XML);

        assertEquals("A 200 response should be returned", Response.Status.OK.getStatusCode(),
                clientResponse.getStatus());

        ResultSet res =
                ResultSetFactory.fromXML(clientResponse.getEntityInputStream());

        ResultSetRewindable resrw = ResultSetFactory.makeRewindable(res);

        //ResultSetFormatter.out(System.err, resrw);

        assertEquals(6, resrw.size()); // got the two public and one private type
    }

    @Test
    public void testDereiQueryUnauthenticated() {

        ClientResponse clientResponse = createGetClientResponse(null, null,
                allRelURI, RdfMediaType.APPLICATION_XML);

        assertEquals("A 200 response should be returned", Response.Status.OK.getStatusCode(),
                clientResponse.getStatus());

        ResultSet res =
                ResultSetFactory.fromXML(clientResponse.getEntityInputStream());

        ResultSetRewindable resrw = ResultSetFactory.makeRewindable(res);

        //ResultSetFormatter.out(System.err, resrw);

        assertEquals(4, resrw.size()); // got the two public and one private type
    }
    
    @Test
    public void testQueryTypes() throws UnsupportedEncodingException {

        ClientResponse clientResponse = createGetClientResponse(null, null,
                makeQueryAnno("select (count(*) as ?c) { graph ?g { ?s ?p ?o } }"), 
                RdfMediaType.APPLICATION_XML);

        assertEquals("A 200 response should be returned", Response.Status.OK.getStatusCode(),
                clientResponse.getStatus());

        ResultSet res =
                ResultSetFactory.fromXML(clientResponse.getEntityInputStream());

        ResultSetRewindable resrw = ResultSetFactory.makeRewindable(res);
        
        RDFNode count = resrw.next().get("c");
        
        assertNotNull(count);
        assertTrue(count.isLiteral());
        
        ResultSetFormatter.out(System.err, resrw);
        
        //assertEquals(4, resrw.size()); // got the two public and one private type
    }
    
    @Test
    public void testConstructType() throws UnsupportedEncodingException {

        ClientResponse clientResponse = createGetClientResponse(null, null,
                makeQueryAnno("construct { ?s ?p ?o } { graph ?g { ?s ?p ?o } }"), 
                RdfMediaType.APPLICATION_RDF_XML);
        
        assertEquals("A 200 response should be returned", Response.Status.OK.getStatusCode(),
                clientResponse.getStatus());
        
        Model m = ModelFactory.createDefaultModel();
        
        m.read(clientResponse.getEntityInputStream(), "RDF/XML");
        
        assertEquals(14, m.size());
    }
    
    @Test
    public void testDescribeType() throws UnsupportedEncodingException {

        ClientResponse clientResponse = createGetClientResponse(null, null,
                makeQueryAnno("describe ?s { graph ?g { ?s ?p ?o } }"), 
                RdfMediaType.APPLICATION_RDF_XML);
        
        assertEquals("A 200 response should be returned", Response.Status.OK.getStatusCode(),
                clientResponse.getStatus());
        
        Model m = ModelFactory.createDefaultModel();
        
        m.read(clientResponse.getEntityInputStream(), "RDF/XML");
        
        //m.write(System.err, "TURTLE");
        
        assertEquals(21, m.size());
    }
    
    @Test
    public void testDescribeTypeLit() throws UnsupportedEncodingException {

        ClientResponse clientResponse = createGetClientResponse(null, null,
                makeQueryAnno("describe <urn:x-foo:notthere>"), 
                RdfMediaType.APPLICATION_RDF_XML);
        
        assertEquals("A 200 response should be returned", Response.Status.OK.getStatusCode(),
                clientResponse.getStatus());
        
        Model m = ModelFactory.createDefaultModel();
        
        m.read(clientResponse.getEntityInputStream(), "RDF/XML");
        
        //m.write(System.err, "TURTLE");
        
        assertEquals(0, m.size());
    }
    
    @Test
    public void testQueryPOST() throws UnsupportedEncodingException {

        ClientResponse clientResponse = createPostClientResponse(null, null,
                baseUri + "query/annotations",
                RdfMediaType.APPLICATION_XML,
                "query=" + URLEncoder.encode(QUERY_TYPE, "UTF-8"));

        assertEquals("A 200 response should be returned", Response.Status.OK.getStatusCode(),
                clientResponse.getStatus());

        ResultSet res =
                ResultSetFactory.fromXML(clientResponse.getEntityInputStream());

        ResultSetRewindable resrw = ResultSetFactory.makeRewindable(res);

        assertEquals(2, resrw.size()); // got the two public types
    }
}
