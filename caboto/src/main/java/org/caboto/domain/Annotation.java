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
package org.caboto.domain;

import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.caboto.CabotoUtility;
import org.caboto.profile.Profile;
import org.caboto.profile.ProfileEntry;
import org.caboto.profile.ProfileRepository;
import org.caboto.vocabulary.Annotea;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: Annotation.java 177 2008-05-30 13:50:59Z mike.a.jones $
 */
public final class Annotation {


    public Annotation() {
    }

    public Annotation(final String id, final String graphId, final String annotates,
                      final String author, final Date created, final Map<String, String> body,
                      final String type) {
        this.id = id;
        this.graphId = graphId;
        this.annotates = annotates;
        this.author = author;
        this.created = created;
        this.body = body;
        this.type = type;
    }
    
    public Annotation(Resource resource , ProfileRepository profileRepository) throws AnnotationException {
    	Model model=ModelFactory.createDefaultModel();
    	this.id = resource.getURI().toString();
        this.graphId =CabotoUtility.getGraphId(id);
        this.annotates = resource.getProperty(Annotea.annotates).getString();
        this.author = resource.getProperty(Annotea.author).getString();
        this.type = resource.getProperty(RDF.type).getString();
        Resource bodyResource = resource.getProperty(Annotea.body).getResource();        
        try {
        	this.created = CabotoUtility.parseDate(resource.getProperty(Annotea.created).getString());
            Profile profile = profileRepository.findProfile(type);
            ProfileEntry profileEntry=null;
            String bodyValue="";
            Iterator<ProfileEntry> profileIter = profile.getProfileEntries().iterator();
            while (profileIter.hasNext()){
            	profileEntry=profileIter.next();
            	bodyValue=bodyResource.getProperty(model.createProperty(profileEntry.getPropertyType())).getString();
            	body.put(profileEntry.getId(),bodyValue);
            }
        } catch (Exception e) {
        	throw new AnnotationException(e);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getGraphId() {
        return graphId;
    }

    public void setGraphId(final String graphId) {
        this.graphId = graphId;
    }

    public String getAnnotates() {
        return annotates;
    }

    public void setAnnotates(final String annotates) {
        this.annotates = annotates;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(final String author) {
        this.author = author;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(final Date created) {
    	System.err.println("Date="+created);
        this.created = created;
    }

    public Map<String, String> getBody() {
        return body;
    }

    public void setBody(final Map<String, String> body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String toString() {
        return new StringBuffer("[id: ").append(id).append(";\n")
                .append("graphId: ").append(graphId).append(";\n")
                .append("annotates: ").append(annotates).append(";\n")
                .append("author: ").append(author).append(";\n")
                .append("created: ").append(created.toString()).append(";\n")
                .append("type: ").append(type).append(";\n")
                .append("body: ").append(body.toString())
                .append("]").toString();
    }


    private String id;
    private String graphId;
    private String annotates;
    private String author;
    private Date created;
    private Map<String, String> body = new HashMap<String, String>();
    private String type = "";
}
