/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.caboto.security.sparql;

import org.openjena.atlas.lib.Pair;
import java.util.List;
import java.util.LinkedList;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.ARQConstants;
import com.hp.hpl.jena.sparql.core.QuerySolutionBase;
import com.hp.hpl.jena.sparql.core.describe.DescribeHandler;
import com.hp.hpl.jena.sparql.core.describe.DescribeHandlerFactory;
import com.hp.hpl.jena.sparql.util.Context;
import java.util.Collections;
import java.util.Iterator;

/**
 *
 * @author pldms
 */
public class AnnotationDescriber implements DescribeHandler {
    private Dataset ds;
    private Model model;
    
    private final static Query FQuery = QueryFactory.create(
      "construct { ?s ?p ?o } { graph ?g { ?s ?p ?o } }");
    
    // Explanation: we want describe to find all of the annotation
    // This finds the head and body based on four cases
    // (1) we have the head, (2) we have the body,
    // (3) we have the annotated thing
    // (4) we have something hanging from the body
    private final static Query LocQuery = QueryFactory.create(
"prefix anno:  <http://www.w3.org/2000/10/annotation-ns#>\n" +
"select ?head ?body\n" +
"{\n" +
"  graph ?g {\n" +
"   { ?head anno:body ?body . filter ( ?head = ?s ) }\n" +
"     union\n" +
"   { ?head anno:body ?body . filter ( ?body = ?s ) }\n" +
"     union\n" +
"   { ?head anno:body ?body ; anno:annotates ?s }\n" +
"     union\n" +
"   { ?head anno:body ?body . ?body ?p ?s }\n" +
"  }\n" +
"}"
            );
    
    public void start(Model model, Context cntxt) {
        this.ds = (Dataset) cntxt.get(ARQConstants.sysCurrentDataset);
        this.model = model;
    }

    public void describe(Resource rsrc) {
        QuerySolution initialBinding = new SingletonBinding("s", rsrc);
        QueryExecution execute = QueryExecutionFactory.create(LocQuery, ds, initialBinding);
        ResultSet result = execute.execSelect();
        List<Pair<Resource, Resource>> headsAndBodies = 
                new LinkedList<Pair<Resource, Resource>>();
        while (result.hasNext()) {
            QuerySolution soln = result.next();
            Resource head = soln.getResource("head");
            Resource body = soln.getResource("body");
            headsAndBodies.add(new Pair<Resource, Resource>(head, body));
        }
        execute.close();
        
        for (Pair<Resource, Resource> pair: headsAndBodies) {
            grabValues(pair.car(), model);
            grabValues(pair.cdr(), model);
        }
    }
    
    private void grabValues(Resource rsrc, Model model) {
        QuerySolution initialBinding = new SingletonBinding("s", rsrc);
        QueryExecution execute = QueryExecutionFactory.create(FQuery, ds, initialBinding);
        execute.execConstruct(model);
        execute.close();
    }
    
    public void finish() {}
    
    public static class AnnotationDescriberFactory 
        implements DescribeHandlerFactory {

        public DescribeHandler create() {
            return new AnnotationDescriber();
        }
    
    }
    
    /* Bit silly, but I really don't need a full map */
    public static class SingletonBinding extends QuerySolutionBase {
        private final String var;
        private final RDFNode val;
        
        public SingletonBinding(String var, RDFNode val) {
            this.var = var; this.val = val;
        }
        
        @Override
        protected RDFNode _get(String string) {
            return (var.equals(string)) ? val : null ;
        }

        @Override
        protected boolean _contains(String string) {
            return var.equals(string);
        }

        @Override
        public Iterator<String> varNames() {
            return Collections.singleton(var).iterator();
        }
    
    }
}
