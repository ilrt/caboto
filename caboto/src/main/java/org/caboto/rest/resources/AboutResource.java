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
import org.caboto.CabotoJsonSupport;
import org.caboto.RdfMediaType;
import org.caboto.dao.AnnotationDao;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.caboto.filters.AnnotationFilter;
import org.caboto.filters.AnnotationFilterFactory;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: AboutResource.java 177 2008-05-30 13:50:59Z mike.a.jones $
 */
@Path("/about/")
@Component
@Scope("singleton")
public final class AboutResource {

    @GET
    @Produces({RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.TEXT_RDF_N3})
    public Response findAnnotations(@QueryParam("id") final String about) {
        Model results;
        
        if (about != null) results = annotationDao.findAnnotations(about);
        else {
            AnnotationFilter[] filters =
                AnnotationFilterFactory.getFromParameters(uriInfo.getQueryParameters());
            if (filters.length == 0)
                return Response.status(Response.Status.BAD_REQUEST).build();
            results = annotationDao.findAnnotations(filters);
        }

        if (results.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(results).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAnnotationsAsJson(@QueryParam("id") final String about)
            throws JSONException {

        Model results = annotationDao.findAnnotations(about);

        if (results.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        JSONArray jsonArray = jsonSupport.generateJsonArray(results);

        return Response.status(Response.Status.OK).entity(jsonArray).build();
    }
    
    //@GET
    //@Produces({RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.TEXT_RDF_N3})
    public Response findFAnnotations() {
        AnnotationFilter[] filters =
                AnnotationFilterFactory.getFromParameters(uriInfo.getQueryParameters());
        if (filters.length == 0)
            return Response.status(Response.Status.BAD_REQUEST).build();
        
        Model results = annotationDao.findAnnotations(filters);

        if (results.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(results).build();
    }

    //@GET
    //@Produces(MediaType.APPLICATION_JSON)
    public Response findAnnotationsAsJson()
            throws JSONException {
        AnnotationFilter[] filters =
                AnnotationFilterFactory.getFromParameters(uriInfo.getQueryParameters());

        if (filters.length == 0)
            return Response.status(Response.Status.BAD_REQUEST).build();

        Model results = annotationDao.findAnnotations(filters);

        if (results.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        JSONArray jsonArray = jsonSupport.generateJsonArray(results);

        return Response.status(Response.Status.OK).entity(jsonArray).build();
    }

    @Context
    private UriInfo uriInfo = null;

    @Autowired
    @Qualifier("annotationDaoProxy")
    private AnnotationDao annotationDao = null;

    @Autowired
    private CabotoJsonSupport jsonSupport = null;
}
