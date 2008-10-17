/*
 * @(#)Database.java
 * Created: 20 Aug 2008
 * Version: 1.0
 * Copyright (c) 2005-2006, University of Manchester All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the University of
 * Manchester nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
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
 */

package org.caboto.jena.db;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * Hides access to the database (in case we change it later)
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public interface Database {

    /**
     * Executes the given SPARQL select query on the database
     *
     * @param sparql          The query
     * @param initialBindings The values for the variables in the query
     * @return The results of the query
     */
    Results executeSelectQuery(final String sparql,
                               final QuerySolution initialBindings);

    /**
     * Executes the given SPARQL construct query on the database
     *
     * @param sparql          The query
     * @param initialBindings The values for the variables in the query
     * @return The results of the query
     */
    Model executeConstructQuery(final String sparql,
                                final QuerySolution initialBindings);

    /**
     * Gets a model that can be used to update the database using the methods
     * in this interface
     *
     * @return The model that can be altered
     */
    Model getUpdateModel();

    /**
     * Adds a model to the database
     *
     * @param uri   The uri of the model (null for default graph)
     * @param model The model to add
     * @return true if the model was added, false if not
     */
    boolean addModel(String uri, Model model);

    /**
     * Deletes a model from the database
     *
     * @param uri   The uri of the model (null for default graph)
     * @param model The model to delete
     * @return true if the model was deleted, false if not
     */
    boolean deleteModel(String uri, Model model);

    /**
     * Replaces the value of a property of a resource
     *
     * @param uri         The uri of the graph (null for default graph)
     * @param resourceUri The uri of the resource to update
     * @param property    The property to update
     * @param value       The value to replace the current value with
     * @return true if the value was updated, false if not
     */
    boolean updateProperty(String uri, String resourceUri, Property property,
                           RDFNode value);
}
