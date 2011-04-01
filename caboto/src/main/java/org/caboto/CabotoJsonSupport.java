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
package org.caboto;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * A utility class used to convert RDF (Jena Model and Resource classes) to JSON.
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: CabotoJsonSupport.java 177 2008-05-30 13:50:59Z mike.a.jones $
 */
public class CabotoJsonSupport {

    public CabotoJsonSupport() {
    }

    public JSONArray generateJsonArray(Model model) throws JSONException {

        JSONArray jsonArray = new JSONArray();

        ResIterator iter = model.listSubjects();

        while (iter.hasNext()) {

            Resource r = (Resource) iter.next();

            if (!r.getURI().endsWith("#body")) {
                jsonArray.put(generateJsonObject(r));
            }

        }

        return jsonArray;
    }

    public JSONObject generateJsonObject(Resource resource) throws JSONException {
    	return generateJsonObject(resource, false);
    }
    
    public JSONObject generateJsonObject(Resource resource, boolean multiValued) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", resource.getURI());

        // get the statements that make up the resource
        StmtIterator stmtIter = resource.listProperties();

        while (stmtIter.hasNext()) {

            // get the statment
            Statement stmt = (Statement) stmtIter.next();

            String key = getlocalName(stmt.getPredicate());
            
            if (multiValued) {
                if(!jsonObject.has(key)) jsonObject.put(key, new JSONArray());
                jsonObject.getJSONArray(key).put(getValue(key, stmt.getObject()));
            } else {
                jsonObject.put(key, getValue(key, stmt.getObject()));
            }

        }
        return jsonObject;
    }
    
    private Object getValue(String key, RDFNode node) throws JSONException {
        if (node.isLiteral()) return node.asLiteral().getLexicalForm();
        
        Resource res = node.asResource();
        if (res.hasProperty(null)) return generateJsonObject(res, true); // body is multivalued
        else {
            if (key.equals("type")) return getlocalName(res);
            else return res.getURI();
        }
    }
    
    private String getlocalName(Resource resource) {

        // use the local name as the key if possible
        if (resource.getLocalName() != null) {
            return resource.getLocalName();
        } else {
            return resource.getURI();
        }
    }

}
