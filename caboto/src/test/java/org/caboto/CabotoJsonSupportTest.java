/*
 * Copyright (c) 2011, University of Bristol
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
package org.caboto;

import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import org.caboto.vocabulary.Annotea;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
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
    public void testGenerateJsonObject_Literal() throws Exception {
        body.addProperty(DC.subject, "subj1");
        
        CabotoJsonSupport instance = new CabotoJsonSupport();
        String expResult = 
                "{\"body\":{\"subject\":[\"subj1\"]},\"annotates\":\"http:\\/\\/example.com\\/annotated\",\"type\":\"TheType\"}";
        JSONObject result = instance.generateJsonObject(resource);
        
        assertEquals(expResult, result.toString());
        
        body.addProperty(DC.subject, "subj2");
        
        expResult = 
                "{\"body\":{\"subject\":[\"subj2\",\"subj1\"]},\"annotates\":\"http:\\/\\/example.com\\/annotated\",\"type\":\"TheType\"}";
        
        result = instance.generateJsonObject(resource);
        
        assertEquals(expResult, result.toString());
    }
    
    /**
     * Test of generateJsonObject method, of class CabotoJsonSupport.
     */
    @Test
    public void testGenerateJsonObject_Resource() throws Exception {
        body.addProperty(DCTerms.provenance, body.getModel().createResource("http://example.com/doc/1"));
        
        CabotoJsonSupport instance = new CabotoJsonSupport();
        String expResult = 
                "{\"body\":{\"provenance\":[\"http:\\/\\/example.com\\/doc\\/1\"]},\"annotates\":\"http:\\/\\/example.com\\/annotated\",\"type\":\"TheType\"}";
        JSONObject result = instance.generateJsonObject(resource);
        
        assertEquals(expResult, result.toString());
        
        body.addProperty(DCTerms.provenance, body.getModel().createResource("http://example.com/doc/2"));
        
        expResult = 
                "{\"body\":{\"provenance\":[\"http:\\/\\/example.com\\/doc\\/2\",\"http:\\/\\/example.com\\/doc\\/1\"]},\"annotates\":\"http:\\/\\/example.com\\/annotated\",\"type\":\"TheType\"}";
        
        result = instance.generateJsonObject(resource);
        
        assertEquals(expResult, result.toString());
    }
}