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
package org.caboto.rest.resources;

import com.hp.hpl.jena.rdf.model.Resource;
import com.sun.jersey.spi.inject.Inject;
import com.sun.jersey.spi.resource.PerRequest;
import org.caboto.CabotoJsonSupport;
import org.caboto.dao.AnnotationDao;
import org.caboto.dao.AnnotationDaoException;
import org.caboto.domain.Annotation;
import org.caboto.domain.AnnotationFactory;
import org.caboto.profile.ProfileRepositoryException;
import org.caboto.profile.ProfileRepositoryXmlImpl;
import org.caboto.validation.AnnotationValidatorImpl;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: PersonPublicAnnotationResource.java 177 2008-05-30 13:50:59Z mike.a.jones $
 *
 **/
@PerRequest
@Path("/person/{uid}/public/")
public final class PersonPublicAnnotationResource {

    @POST
    @ConsumeMime(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addAnnotation(@PathParam("uid") final String uid,
                                  final MultivaluedMap<String, String> params)
            throws AnnotationDaoException, ProfileRepositoryException, URISyntaxException {

        // the uid in the URI *must* match the principal name
        if (securityContext.getUserPrincipal() == null ||
                !securityContext.getUserPrincipal().getName().equals(uid)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        // encapsulate the post parameters into a useful object
        Annotation annotation = AnnotationFactory.createAnnotation(uriInfo.getRequestUri(), params);

        Validator validator = new AnnotationValidatorImpl(
                new ProfileRepositoryXmlImpl("profiles.xml"));

        //validate what is sent
        Errors errors = new BeanPropertyBindingResult(annotation, "Annotation");

        validator.validate(annotation, errors);

        if (errors.hasErrors()) {

            //for (Object e : errors.getAllErrors()) {
            //    System.out.println("> " + ((FieldError) e).getCode());
            //}

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Oops, there are validation errors!\n").build();
        }

        // add what is sent to the RDF store
        annotationDao.addAnnotation(annotation);

        // return the URI of the added annotation
        return Response.created(new URI(annotation.getId())).build();
    }


    @Path("{id}")
    @GET
    @ProduceMime({"application/rdf+xml", "text/rdf+n3"})
    public Response getAnnotation() throws AnnotationDaoException {

        Resource resource = annotationDao.findAnnotation(uriInfo.getRequestUri().toString());

        if (resource.getModel().isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.status(Response.Status.OK).entity(resource).build();
        }

    }


    @Path("{id}")
    @GET
    @ProduceMime(MediaType.APPLICATION_JSON)
    public Response getAnnotationAsJson() throws AnnotationDaoException, JSONException {

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
    public Response deleteAnnotation(@PathParam("uid") final String uid)
            throws AnnotationDaoException {

        Resource resource = annotationDao.findAnnotation(uriInfo.getRequestUri().toString());

        if (resource.getModel().isEmpty()) {
            Response.status(Response.Status.NOT_FOUND).build();
        }

        if (securityContext.getUserPrincipal() != null) {
            if (securityContext.getUserPrincipal().getName().equals(uid) ||
                    securityContext.isUserInRole("ADMIN")) {
                annotationDao.deleteAnnotation(resource);
                return Response.ok().build();
            }
        }

        return Response.status(Response.Status.UNAUTHORIZED).build();

    }

    @Context
    private UriInfo uriInfo = null;

    @Context
    private SecurityContext securityContext = null;

    @Inject
    private AnnotationDao annotationDao = null;

    @Inject
    private CabotoJsonSupport jsonSupport = null;

}
