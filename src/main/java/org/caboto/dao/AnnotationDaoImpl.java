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

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.vocabulary.RDF;
import org.caboto.CabotoUtility;
import org.caboto.domain.Annotation;
import org.caboto.profile.Profile;
import org.caboto.profile.ProfileEntry;
import org.caboto.profile.ProfileRepository;
import org.caboto.profile.ProfileRepositoryException;
import org.caboto.vocabulary.Annotea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

/**
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 *
 **/
public class AnnotationDaoImpl implements AnnotationDao {

    public AnnotationDaoImpl(String sdbConfigFile, ProfileRepository profileRepository) {
        this.sdbConfigFile = sdbConfigFile;
        this.profileRepository = profileRepository;
        findAnnotationSparql = loadSparqlFromFile(findAnnotation);
        initStore();
    }

    public void addAnnotation(Annotation annotation) throws AnnotationDaoException {

        try {

            // find the profile for the annotation type
            Profile profile = profileRepository.findProfile(annotation.getType());

            if (profile == null) {
                throw new AnnotationDaoException("Unable to find a profile for " +
                        "the annotation type: " + annotation.getType());
            }

            // obtain the named graph (model)
            Model model = SDBFactory.connectNamedModel(store, annotation.getGraphId());

            model.setNsPrefix("caboto", "http://caboto.org/schema/annotations#");
            model.setNsPrefix("annotea", "http://www.w3.org/2000/10/annotation-ns#");

            // --- CREATE THE STANDARD ANNOTATION DETAILS ---

            // generate uri and resource for the annotation
            String uri = CabotoUtility.generateId(annotation.getGraphId());
            Resource annotationResource = model.createResource(uri);

            // what is being annotated?
            annotationResource.addProperty(Annotea.annotates,
                    model.createResource(annotation.getAnnotates()));

            // who made the annotation?
            annotationResource.addProperty(Annotea.author,
                    model.createResource(annotation.getAuthor()));

            Date created = new Date();

            // creation date
            annotationResource.addProperty(Annotea.created,
                    model.createTypedLiteral(CabotoUtility.parseDate(created),
                            XSDDatatype.XSDdateTime));

            annotation.setId(uri);
            annotation.setCreated(created);

            // --- CREATE THE BODY OF THE ANNOTATION ---

            // create a  uri and resource
            String bodyUri = uri + "#body";
            Resource bodyResource = model.createResource(bodyUri);


            for (ProfileEntry entry : profile.getProfileEntries()) {

                String val = annotation.getBody().get(entry.getId());

                Property prop = model.createProperty(entry.getPropertyType());

                String dataType = entry.getObjectDatatype();

                if (dataType != null) {

                    if (dataType.equals("String")) {
                        bodyResource.addProperty(prop, val, XSDDatatype.XSDstring);
                    }

                } else {
                    bodyResource.addProperty(prop, val);
                }

            }

            // add the body to the resource
            annotationResource.addProperty(Annotea.body, bodyResource);

            // add the type as a statement 
            model.add(model.createStatement(annotationResource, RDF.type,
                    model.createResource(profile.getType())));

            //model.write(System.out);


        } catch (ProfileRepositoryException e) {
            throw new AnnotationDaoException(e.getMessage());
        }


    }

    public Resource findAnnotation(String id) throws AnnotationDaoException {

        // extract the graph from the id
        String graph = id.substring(0, (id.lastIndexOf('/') + 1));

        // obtain the dataset
        Dataset dataset = SDBFactory.connectDataset(store);

        // create bindings
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("id", ResourceFactory.createResource(id));
        initialBindings.add("graph", ResourceFactory.createResource(graph));

        Query query = QueryFactory.create(findAnnotationSparql);

        // execute query
        QueryExecution qe = QueryExecutionFactory.create(query, dataset, initialBindings);

        Model m = qe.execConstruct();

        return m.createResource(id);
    }

    public Model findAnnotations(String about) throws AnnotationDaoException {

        // obtain the dataset
        Dataset dataset = SDBFactory.connectDataset(store);

        // create bindings
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("annotates", ResourceFactory.createResource(about));

        Query query = QueryFactory.create(findAnnotationSparql);

        // execute query
        QueryExecution qe = QueryExecutionFactory.create(query, dataset, initialBindings);

        Model m = qe.execConstruct();

        m.write(System.out);
        
        return m;


    }


    private String loadSparqlFromFile(final String sparqlPath) {

        StringBuffer buffer = new StringBuffer();

        try {

            InputStream is = this.getClass().getResourceAsStream(sparqlPath);
            BufferedReader d = new BufferedReader(new InputStreamReader(is));

            String s;

            while ((s = d.readLine()) != null) {
                buffer.append(s);
                buffer.append("\n");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return buffer.toString();
    }


    private void initStore() {
        String storePath = this.getClass().getResource(sdbConfigFile).getPath();
        store = SDBFactory.connectStore(storePath);
    }


    private Store store;
    private ProfileRepository profileRepository;
    private String findAnnotationSparql;
    private String findAnnotation = "/sparql/findAnnotation.rql";
    private String sdbConfigFile;
}