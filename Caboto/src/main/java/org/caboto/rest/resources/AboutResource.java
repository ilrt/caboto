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

import com.hp.hpl.jena.rdf.model.Model;
import com.sun.jersey.spi.resource.Inject;
import com.sun.jersey.spi.resource.PerRequest;
import org.caboto.CabotoJsonSupport;
import org.caboto.dao.AnnotationDao;
import org.caboto.dao.AnnotationDaoException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 *
 **/
@PerRequest
@Path("/about/")
public class AboutResource {

    @GET
    @ProduceMime({"application/rdf+xml", "text/rdf+n3"})
    public Response findAnnotations(@QueryParam("id")String about) {

        try {
            Model results = annotationDao.findAnnotations(about);

            if (results.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            return Response.status(Response.Status.OK).entity(results).build();

        } catch (AnnotationDaoException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GET
    @ProduceMime(MediaType.APPLICATION_JSON)
    public Response findAnnotationsAsJson(@QueryParam("id")String about) throws JSONException {

        try {
            Model results = annotationDao.findAnnotations(about);


            if (results.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            JSONArray jsonArray = jsonSupport.generateJsonArray(results);

            return Response.status(Response.Status.OK).entity(jsonArray).build();

        } catch (AnnotationDaoException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    @Inject
    private AnnotationDao annotationDao = null;

    @Inject
    private CabotoJsonSupport jsonSupport = null;
}
