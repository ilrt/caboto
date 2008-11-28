/*
 * Copyright (c) 2008, University of Bristol
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
 * 3) Neither the name of the University of Bristol nor the names of its
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
package org.caboto.dao;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import org.caboto.RdfMediaType;
import org.caboto.domain.Annotation;
import org.caboto.domain.AnnotationException;
import org.caboto.domain.AnnotationFactory;
import org.caboto.rest.providers.JenaModelRdfProvider;
import org.caboto.rest.providers.JenaResourceRdfProvider;
import org.caboto.rest.resources.BasicAuthenticationClientFilter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.Response;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: AnnotationDaoImpl.java 177 2008-05-30 13:50:59Z mike.a.jones $
 */
public final class AnnotationDaoRestImpl implements AnnotationDao {

    private final String uri;
    private final String username;
    private final String password;


    public AnnotationDaoRestImpl(final String uri, final String username, final String password, final boolean publicAnnotation) {
        this.uri=uri+"/person/"+username+"/"+(publicAnnotation?"public":"private")+"/";
        this.username=username;
        this.password=password;
    }

    public void addAnnotation(final Annotation annotation) {
        Map<String,String> annotationBody=annotation.getBody();
        Iterator<Entry<String,String>> iter= annotationBody.entrySet().iterator();
        String postData;
        try {
            postData = "annotates="+URLEncoder.encode(annotation.getAnnotates(),"UTF-8");
            postData+="&created="+annotation.getCreated();
            postData+="&type="+annotation.getType();

            Entry<String, String> e;
            while (iter.hasNext()){
                e=(Entry<String, String>) iter.next();
                postData+="&"+e.getKey()+"="+e.getValue();
            }

            Client c = Client.create();

            if (username != null && password != null) {
                c.addFilter(new BasicAuthenticationClientFilter(username, password));
            }

            ClientResponse clientResponse = c.resource(uri).type(annotation.getType()).post(ClientResponse.class, postData);
            if (clientResponse.getStatus()!=Response.Status.CREATED.getStatusCode())
                throw new RuntimeException(clientResponse.toString());
            annotation.setId(clientResponse.getLocation().toString());
        } catch (UnsupportedEncodingException e1) {
            throw new RuntimeException(e1);
        }
    }

    public Resource findAnnotation(final String id) {


        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(JenaResourceRdfProvider.class);
        config.getClasses().add(JenaModelRdfProvider.class);
        Client c = Client.create(config);

        if (username != null && password != null) {
            c.addFilter(new BasicAuthenticationClientFilter(username, password));
        }

        return c.resource(id).accept(RdfMediaType.APPLICATION_RDF_XML_TYPE).get(Resource.class);
    }

    public Annotation getAnnotation(final String id){
        Annotation annotation=null;
        try {
            annotation=new Annotation(findAnnotation(id),null);
        } catch (AnnotationException e) {
            e.printStackTrace();
        }
        return annotation;
    }

    public Model findAnnotations(final String about) {

        Annotation annotation = new Annotation();
        annotation.setAnnotates(about);

        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(JenaResourceRdfProvider.class);
        config.getClasses().add(JenaModelRdfProvider.class);

        Client c = Client.create(config);

        if (username != null && password != null) {
            c.addFilter(new BasicAuthenticationClientFilter(username, password));
        }

        ClientResponse clientResponse = c.resource(uri).accept(RdfMediaType.TEXT_RDF_N3).get(ClientResponse.class);

        Model model = clientResponse.getEntity(Model.class);

        return model;
    }

    public  List<Annotation> getAnnotations(final String about){
        Model annModel = findAnnotations(about);
        return AnnotationFactory.annotationsFromModel(annModel,null);
    }


    public Model findAnnotationsByAuthor(final String author) {

        Annotation annotation = new Annotation();
        annotation.setAuthor(author);

        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(JenaResourceRdfProvider.class);
        config.getClasses().add(JenaModelRdfProvider.class);

        Client c = Client.create(config);

        if (username != null && password != null) {
            c.addFilter(new BasicAuthenticationClientFilter(username, password));
        }

        ClientResponse clientResponse = c.resource(uri).accept(RdfMediaType.TEXT_RDF_N3).get(ClientResponse.class);

        Model model = clientResponse.getEntity(Model.class);

        return model;
    }

    public  List<Annotation> getAnnotationsByAuthor(final String author){
        Model annModel = findAnnotationsByAuthor(author);
        return AnnotationFactory.annotationsFromModel(annModel,null);
    }


    public void deleteAnnotation(final Resource resource) {

        String url = resource.getURI();
        // delete the resource
        Client c = Client.create();
        ClientResponse deleteResponse = c.resource(url).delete(ClientResponse.class);
        if (deleteResponse.getStatus()!=Response.Status.OK.getStatusCode())
            throw new RuntimeException(deleteResponse.toString());
    }

    public Model findAnnotationsByGraph(String graph) {
        // TODO Auto-generated method stub
        return null;
    }


}
