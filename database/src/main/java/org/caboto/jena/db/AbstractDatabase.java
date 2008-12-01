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

package org.caboto.jena.db;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
/**
 * A useful database abstraction that implements the details of the queries,
 * but keeps the database implementation free
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public abstract class AbstractDatabase implements Database {

    /**
     * Gets the data object of the database for querying
     *
     * @return The data object
     * @throws DataException If there is an error getting the data
     */
    public abstract Data getData() throws DataException;

    /**
     * @see org.caboto.jena.db.Database#executeSelectQuery(java.lang.String,
     *     com.hp.hpl.jena.query.QuerySolution)
     */
    public Results executeSelectQuery(String sparql,
            QuerySolution initialBindings) {
        try {
            Data data = getData();
            Dataset dataset = data.getDataset();
            QueryExecution queryExec;
            if (initialBindings != null) {
                queryExec = QueryExecutionFactory.create(sparql, dataset,
                        initialBindings);
            } else {
                queryExec = QueryExecutionFactory.create(sparql, dataset);
            }

            return new Results(queryExec.execSelect(), queryExec, data);
        } catch (DataException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * @see org.caboto.jena.db.Database#executeConstructQuery(com.hp.hpl.jena.query.Query,
     *     com.hp.hpl.jena.query.QuerySolution)
     */
    public Model executeConstructQuery(Query query,
            QuerySolution initialBindings) {
        try {
            Data data = getData();
            Dataset dataset = data.getDataset();
            QueryExecution queryExec;
            if (initialBindings != null) {
                queryExec = QueryExecutionFactory.create(query, dataset,
                        initialBindings);
            } else {
                queryExec = QueryExecutionFactory.create(query, dataset);
            }
            Model model = queryExec.execConstruct();
            queryExec.close();
            data.close();
            return model;
        } catch (DataException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * @see org.caboto.jena.db.Database#executeConstructQuery(java.lang.String,
     *      com.hp.hpl.jena.query.QuerySolution)
     */
    public Model executeConstructQuery(String sparql,
                                       QuerySolution initialBindings) {
        Query query = QueryFactory.create(sparql);
        return executeConstructQuery(query, initialBindings);
    }

    /**
     * @see org.caboto.jena.db.Database#getUpdateModel()
     */
    public Model getUpdateModel() {
        return ModelFactory.createDefaultModel();
    }

    /**
     * @see org.caboto.jena.db.Database#addModel(java.lang.String,
     *     com.hp.hpl.jena.rdf.model.Model)
     */
    public boolean addModel(String uri, Model model) {
        try {
            Data data = getData();
            Model m = data.getModel(uri);
            m.withDefaultMappings(model);
            m.add(model);
            m.close();
            data.close();
            return true;
        } catch (DataException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @see org.caboto.jena.db.Database#deleteModel(java.lang.String,
     *     com.hp.hpl.jena.rdf.model.Model)
     */
    public boolean deleteModel(String uri, Model model) {
        try {
            Data data = getData();
            Model m = data.getModel(uri);
            m.withDefaultMappings(model);
            m.remove(model);
            m.close();
            data.close();
            return true;
        } catch (DataException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAll(String uri) {
        try {
            Data data = getData();
            Model m = data.getModel(uri);
            m.removeAll();
            m.close();
            data.close();
            return true;
        } catch (DataException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * @see org.caboto.jena.db.Database#updateProperty(java.lang.String,
     *      java.lang.String,
     *      com.hp.hpl.jena.rdf.model.Property,
     *      com.hp.hpl.jena.rdf.model.RDFNode)
     */
    public boolean updateProperty(String uri, String resourceUri,
                                  Property property, RDFNode value) {
        try {
            Data data = getData();
            Model m = data.getModel(uri);
            if (!m.containsResource(
                    ResourceFactory.createResource(resourceUri))) {
                m.close();
                data.close();
                return false;
            }
            Resource resource = m.getResource(resourceUri);
            if (resource.hasProperty(property)) {
                resource.getProperty(property).changeObject(value);
            } else {
                resource.addProperty(property, value);
            }
            m.close();
            data.close();
            return true;
        } catch (DataException e) {
            e.printStackTrace();
            return false;
        }
    }
}
