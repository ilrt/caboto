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
package org.caboto.dao;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.caboto.CabotoUtility;
import org.caboto.domain.Annotation;
import org.caboto.domain.AnnotationException;
import org.caboto.domain.AnnotationFactory;
import org.caboto.jena.db.Database;
import org.caboto.jena.db.Utils;
import org.caboto.profile.Profile;
import org.caboto.profile.ProfileEntry;
import org.caboto.profile.ProfileRepository;
import org.caboto.profile.ProfileRepositoryException;
import org.caboto.vocabulary.Annotea;

import java.util.Date;
import java.util.List;
import org.caboto.filters.AnnotationFilter;
import org.caboto.filters.AnnotationFilterFactory;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: AnnotationDaoImpl.java 177 2008-05-30 13:50:59Z mike.a.jones $
 */
public final class AnnotationDaoImpl implements AnnotationDao {

    public AnnotationDaoImpl(final ProfileRepository profileRepository,
                             final Database database) {
        this.profileRepository = profileRepository;
        String findAnnotation = "/sparql/findAnnotation.rql";
        findAnnotationSparql = Utils.loadSparql(findAnnotation);
        this.database = database;
    }


    public void addAnnotation(final Annotation annotation) {

        try {

            // find the profile for the annotation type
            Profile profile = profileRepository.findProfile(annotation.getType());

            if (profile == null) {
                throw new RuntimeException("Unable to find a profile for "
                        + "the annotation type: " + annotation.getType());
            }

            // obtain the named graph (model)
            Model model = database.getUpdateModel();

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

            // creation date
            if (annotation.getCreated()==null){
            	annotation.setCreated(new Date());
            }
            annotationResource.addProperty(Annotea.created,
                    model.createTypedLiteral(CabotoUtility.parseDate(annotation.getCreated()),
                            XSDDatatype.XSDdateTime));

            annotation.setId(uri);

            // --- CREATE THE BODY OF THE ANNOTATION ---

            // create a  uri and resource
            String bodyUri = uri + "#body";
            Resource bodyResource = model.createResource(bodyUri);


            for (ProfileEntry entry : profile.getProfileEntries()) {

                String val = annotation.getBody().get(entry.getId());
                if ((val == null) && !entry.isRequired()){
                	continue;
                }

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

            database.addModel(annotation.getGraphId(), model);

        } catch (ProfileRepositoryException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Resource findAnnotation(final String id) {

        // extract the graph from the id
        String graph = id.substring(0, (id.lastIndexOf('/') + 1));

        // create bindings
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("id", ResourceFactory.createResource(id));
        initialBindings.add("graph", ResourceFactory.createResource(graph));

        Model m = database.executeConstructQuery(findAnnotationSparql,
                initialBindings);

        return m.createResource(id);
    }

    public Model findAnnotationsByGraph(String graph,
            AnnotationFilter... filters) {

        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("graph", ResourceFactory.createResource(graph));
        Query query = QueryFactory.create(findAnnotationSparql);
        AnnotationFilterFactory.applyFilters(query, "body", filters);
        return database.executeConstructQuery(query,
                initialBindings);
    }


    public Model findAnnotations(final String about,
            AnnotationFilter... filters) {

        // create bindings
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("annotates", ResourceFactory.createResource(about));
        Query query = QueryFactory.create(findAnnotationSparql);
        AnnotationFilterFactory.applyFilters(query, "body", filters);
        return database.executeConstructQuery(query,
                initialBindings);
    }

    public Model findAnnotations(AnnotationFilter[] filters) {
        // TODO The query may need reordering in this case
        // create bindings
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        Query query = QueryFactory.create(findAnnotationSparql);
        AnnotationFilterFactory.applyFilters(query, "body", filters);
        return database.executeConstructQuery(query,
                initialBindings);
    
    }
        
    public Annotation getAnnotation(final String id){
    	Annotation annotation=null;
		try {
			annotation=new Annotation(findAnnotation(id), profileRepository);
		} catch (AnnotationException e) {
			e.printStackTrace();
		}
    	return annotation;
    }
    
    public  List<Annotation> getAnnotations(final String about){
    	Model annModel = findAnnotations(about);
    	return AnnotationFactory.annotationsFromModel(annModel,profileRepository);
    }

    
    public Model findAnnotationsByAuthor(final String author) {

        // create bindings
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        
        initialBindings.add("author", ResourceFactory.createResource(author));

        Model m = database.executeConstructQuery(findAnnotationSparql,
                initialBindings);

        return m;

    }

    public  List<Annotation> getAnnotationsByAuthor(final String author){
    	Model annModel = findAnnotationsByAuthor(author);
    	return AnnotationFactory.annotationsFromModel(annModel,profileRepository);
    }
    
    public void deleteAnnotation(final Resource resource) {

        String id = resource.getURI();

        // extract the graph from the id
        String graph = id.substring(0, (id.lastIndexOf('/') + 1));
        Model model = database.getUpdateModel();
        model.add(resource.getModel());
        database.deleteModel(graph, model);
    }

    private final ProfileRepository profileRepository;
    private final String findAnnotationSparql;
    private final Database database;
}
