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

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Model;
import org.caboto.CabotoJsonSupport;
import org.caboto.RdfMediaType;
import org.caboto.dao.AnnotationDao;
import org.caboto.domain.Annotation;
import org.caboto.domain.AnnotationFactory;
import org.caboto.profile.ProfileRepositoryException;
import org.caboto.profile.ProfileRepositoryXmlImpl;
import org.caboto.validation.AnnotationValidatorImpl;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.validation.ObjectError;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: PersonPublicAnnotationResource.java 177 2008-05-30 13:50:59Z mike.a.jones $
 */
@Scope("singleton")
@Path("/person/{uid}/{type}/")
@Component
public final class PersonPublicAnnotationResource {

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addAnnotation(final MultivaluedMap<String, String> params)
            throws URISyntaxException {

        // encapsulate the post parameters into a useful object
        Annotation annotation = AnnotationFactory.createAnnotation(uriInfo.getRequestUri(), params);


        Validator validator;
        try {
            validator = new AnnotationValidatorImpl(
                    new ProfileRepositoryXmlImpl("profiles.xml"));
        } catch (ProfileRepositoryException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Unable to configure the validator").build();
        }

        //validate what is sent
        Errors errors = new BeanPropertyBindingResult(annotation, "Annotation");


        validator.validate(annotation, errors);


        if (errors.hasErrors()) {
            
            StringBuilder errorMessage = new StringBuilder();
            for (ObjectError error: errors.getAllErrors()) {
                errorMessage.append(error.toString());
                errorMessage.append("\n");
            }
            
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Oops, there are validation errors!\n" + errorMessage).build();
        }

        // add what is sent to the RDF store
        annotationDao.addAnnotation(annotation);

        // return the URI of the added annotation
        return Response.created(new URI(annotation.getId())).build();
    }

    @GET
    @Produces({RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.TEXT_RDF_N3})
    public Response getAnnotationByGraph() {

        Model model = annotationDao.findAnnotationsByGraph(uriInfo.getRequestUri().toString());

        if (model.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.status(Response.Status.OK).entity(model).build();
        }

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAnnotationByGraphAsJson() throws JSONException {

        Model model = annotationDao.findAnnotationsByGraph(uriInfo.getRequestUri().toString());

        if (model.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {

            JSONArray jsonArray = jsonSupport.generateJsonArray(model);

            return Response.status(Response.Status.OK).entity(jsonArray).build();
        }

    }

    @Path("{id}")
    @GET
    @Produces({RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.TEXT_RDF_N3})
    public Response getAnnotation() {

        Resource resource = annotationDao.findAnnotation(uriInfo.getRequestUri().toString());

        if (resource.getModel().isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.status(Response.Status.OK).entity(resource).build();
        }

    }


    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAnnotationAsJson() throws JSONException {

        Resource resource = annotationDao.findAnnotation(uriInfo.getRequestUri().toString());

        if (resource.getModel().isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {

            JSONObject jsonObject = jsonSupport.generateJsonObject(resource);

            return Response.status(Response.Status.OK).entity(jsonObject).build();
        }

    }

    @Path("{id}")
    @DELETE
    public Response deleteAnnotation() {

        Resource resource = annotationDao.findAnnotation(uriInfo.getRequestUri().toString());

        if (resource.getModel().isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        annotationDao.deleteAnnotation(resource);
        return Response.ok().build();
    }

    @Context
    private UriInfo uriInfo = null;

    @Autowired
    @Qualifier("annotationDao")
    private AnnotationDao annotationDao = null;

    @Autowired
    private CabotoJsonSupport jsonSupport = null;

}
