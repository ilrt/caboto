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

import javax.ws.rs.core.MultivaluedMap;

import org.caboto.CabotoUtility;
import org.caboto.profile.ProfileRepository;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * <p>A simple factory for creating Annotation objects.</p>
 *
 * @author Mike Jones (mike.a.jones@gmail.com)
 * @version $Id: AnnotationFactory.java 177 2008-05-30 13:50:59Z mike.a.jones $
 */
public final class AnnotationFactory {

    /**
     * <p>Private constructor - no public constructor since the class only has static methods.</p>
     */
    private AnnotationFactory() {
    }
    
    
    /**
     * <p>Creates a List of annotation objects based on values received from a rdf Model</p>
     *
     * @param model  the RDF model containing AnnotoationsURI of the named graph that will hold the annotation.
     * @param params the parameters from an HTTP POST.
     * @return an annotation object based on the HTTP POST values.
     */
   
    public static List<Annotation> annotationsFromModel(Model model, ProfileRepository profileRepository){
		List<Annotation> annList = new Vector<Annotation>();
    	ResIterator resIt = model.listSubjects();
		while (resIt.hasNext()){
			Resource annResource = resIt.nextResource();
			Annotation annotation;
			try {
				annotation = new Annotation(annResource,profileRepository);
				annList.add(annotation);
			} catch (AnnotationException e) {
				// resource is not an annotation
			}			
		}
		return annList;
    }

    /**
     * <p>Creates an annotation object based on values received from a map - the map values
     * would have been derived from a HTTP POST.</p>
     *
     * @param uri    the URI of the named graph that will hold the annotation.
     * @param params the parameters from an HTTP POST.
     * @return an annotation object based on the HTTP POST values.
     */
    public static Annotation createAnnotation(final URI uri,
                                              final MultivaluedMap<String, String> params) {

        // the uri of the graphUri for holding annotations
        String graphUri = uri.toString();

        if (!graphUri.endsWith("/")) {
            graphUri = graphUri + "/";
        }

        // calculate the URI of the user
        String authorUri = graphUri.substring(0, graphUri.lastIndexOf("/", graphUri.length() - 2))
                + "/";

        Annotation annotation = new Annotation();
        annotation.setCreated(new Date());
        annotation.setGraphId(graphUri);
        annotation.setAuthor(authorUri);

        if (params.get("annotates") != null) {
            annotation.setAnnotates(params.remove("annotates").get(0));
        }

        if (params.get("type") != null) {
            annotation.setType(params.remove("type").get(0));
        }
        
    	Date date=new Date();
        if (params.get("created") !=null) {
			try {
				date = CabotoUtility.parseDate(params.remove("created").get(0));
			} catch (ParseException e) {
				// do nothing
			}
        } 
        annotation.setCreated(date);

        // copy the rest of map
        for (String key : params.keySet()) {
            annotation.getBody().put(key, params.get(key));
        }

        return annotation;
    }

}
