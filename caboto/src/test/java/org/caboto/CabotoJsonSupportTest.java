/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.caboto;

import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import org.caboto.vocabulary.Annotea;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
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
public class CabotoJsonSupportTest {
    private Resource resource;
    private Resource body;

    public CabotoJsonSupportTest() {
    }

    @Before
    public void setUp() throws Exception {
        Model model = ModelFactory.createDefaultModel();
        resource = model.createResource();
        body = model.createResource();
        resource.addProperty(RDF.type,
                model.createResource("http://example.com/TheType"));
        resource.addProperty(Annotea.annotates,
                model.createResource("http://example.com/annotated"));
        resource.addProperty(Annotea.body, body);
        /*body.addProperty(DC.subject, "subj1");
        body.addProperty(DC.subject, "subj2");
        body.addProperty(DCTerms.subject,
                model.createResource("http://example.com/subject/1"));
        body.addProperty(DCTerms.subject,
                model.createResource("http://example.com/subject/2"));*/
        
    }

    /**
     * Test of generateJsonObject method, of class CabotoJsonSupport.
     */
    @Test
    public void testGenerateJsonObject_Resource() throws Exception {
        body.addProperty(DC.subject, "subj1");
        
        CabotoJsonSupport instance = new CabotoJsonSupport();
        String expResult = 
                "{\"body\":{\"subject\":[\"subj1\"]},\"annotates\":\"http:\\/\\/example.com\\/annotated\",\"type\":\"TheType\"}";
        JSONObject result = instance.generateJsonObject(resource);
        System.err.print(result.toString());
        assertEquals(expResult, result.toString());
        
        body.addProperty(DC.subject, "subj2");
        
        expResult = 
                "{\"body\":{\"subject\":[\"subj1\"]},\"annotates\":\"http:\\/\\/example.com\\/annotated\",\"type\":\"TheType\"}";
    }

}