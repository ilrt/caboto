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
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.reasoner.rulesys.Rule.Parser;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.sparql.util.Symbol;
import com.hp.hpl.jena.util.FileManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A useful database abstraction that implements the details of the queries,
 * but keeps the database implementation free
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public abstract class AbstractDatabase implements Database {

    final static Log log = LogFactory.getLog(AbstractDatabase.class);

    private Context context;
    private GenericRuleReasoner reasoner;

    /**
     * Gets the data object of the database for querying
     *
     * @return The data object
     * @throws DataException If there is an error getting the data
     */
    public abstract Data getData() throws DataException;
    
    private QueryExecution getQueryExecution(String sparql, QuerySolution bindings, Data data)
            throws DataException {
        Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
        return getQueryExecution(query, bindings, data);
    }
    
    private QueryExecution getQueryExecution(Query query, QuerySolution bindings, Data data)
            throws DataException {
        Dataset dataset = data.getDataset();
        QueryExecution queryExec;
        if (bindings != null) {
            queryExec = QueryExecutionFactory.create(query, dataset,
                    bindings);
        } else {
            queryExec = QueryExecutionFactory.create(query, dataset);
        }
        // Add context items, if there are any
        if (getQueryContext() != null) {
            queryExec.getContext().setAll(getQueryContext());
        }
        return queryExec;
    }
    
    /**
     * @see org.caboto.jena.db.Database#executeSelectQuery(java.lang.String,
     *      com.hp.hpl.jena.query.QuerySolution)
     */
    public Results executeSelectQuery(String sparql,
                                      QuerySolution initialBindings) {
        try {
            Data data = getData();
            QueryExecution queryExec = getQueryExecution(sparql, initialBindings, data);
            return new Results(queryExec.execSelect(), queryExec, data);
        } catch (DataException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * @see org.caboto.jena.db.Database#executeAskQuery(java.lang.String,
     *      com.hp.hpl.jena.query.QuerySolution)
     */
    public Boolean executeAskQuery(String sparql,
                                      QuerySolution initialBindings) {
        try {
            Data data = getData();
            QueryExecution queryExec = getQueryExecution(sparql, initialBindings, data);
            boolean result = queryExec.execAsk();
            queryExec.close();
            data.close();
            return result;
        } catch (DataException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * @see org.caboto.jena.db.Database#executeDescribeQuery(java.lang.String,
     *      com.hp.hpl.jena.query.QuerySolution)
     */
    public Model executeDescribeQuery(String sparql,
                                      QuerySolution initialBindings) {
        try {
            Data data = getData();
            QueryExecution queryExec = getQueryExecution(sparql, initialBindings, data);Model result = queryExec.execDescribe();
            queryExec.close();
            data.close();
            return result;
        } catch (DataException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * @see org.caboto.jena.db.Database#executeConstructQuery(com.hp.hpl.jena.query.Query,
     *      com.hp.hpl.jena.query.QuerySolution)
     */
    public Model executeConstructQuery(Query query,
                                       QuerySolution initialBindings) {
        try {
            Data data = getData();
            QueryExecution queryExec = getQueryExecution(query, initialBindings, data);Model model = queryExec.execConstruct();
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
        Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
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
     *      com.hp.hpl.jena.rdf.model.Model)
     */
    public boolean addModel(String uri, Model model) {
        try {
            log.info("Adding data to model: " + uri + " " + reasoner);
            Data data = getData();
            Model m = data.getModel(uri);
            m.withDefaultMappings(model);
            m.add(model);
            if (reasoner != null) redoInferred(uri, m, false);
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
     *      com.hp.hpl.jena.rdf.model.Model)
     */
    public boolean deleteModel(String uri, Model model) {
        try {
            log.info("Deleteing data in model: " + uri + " " + reasoner);
            Data data = getData();
            Model m = data.getModel(uri);
            m.withDefaultMappings(model);
            m.remove(model);
            if (reasoner != null) redoInferred(uri, m, true);
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
            log.info("Deleting model: " + uri + " " + reasoner);
            Data data = getData();
            Model m = data.getModel(uri);
            m.removeAll();
            if (reasoner != null) redoInferred(uri, m, true);
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
            log.info("Updting property in model: " + uri + " " + reasoner);
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
    
    /**
     * Set context for queries
     */
    public void setQueryContext(Symbol symbol, Object object) {
    	if (this.context == null) context = new Context();
    	this.context.set(symbol, object);
    }
    
    public Context getQueryContext() { return this.context; }

    public void setRules(String rulesFile) {
        try {
            log.info("Trying to load rule file: " + rulesFile);
            InputStream rulesStream = FileManager.get().open(rulesFile);
            Parser rules = Rule.rulesParserFromReader(
                    new BufferedReader(
                    new InputStreamReader(rulesStream, "UTF-8")));
            reasoner = new GenericRuleReasoner(Rule.parseRules(rules));
            log.info("Loaded!");
        } catch (Throwable ex) {
            log.error("Problem loading rules <" + rulesFile + ">", ex);
        }
    }

    public void redoInferred(String uri, Model m, boolean nonMon) throws DataException {
        log.info("Running inferences on " + uri);
        Data data = getData();
        Model infData = data.getModel(uri + "-inferred");
        if (nonMon) infData.removeAll(); // Truth maintenance grossly simplified
        InfModel inf = ModelFactory.createInfModel(reasoner, m);
        infData.add(inf.getDeductionsModel());
        log.info("Inferencing done");
    }
}
