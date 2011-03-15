/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.caboto.security.sparql;

import java.io.StringReader;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.ARQConstants;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.sparql.core.DataSourceImpl;
import com.hp.hpl.jena.sparql.core.describe.DescribeHandler;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Model;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pldms
 */
public class AnnotationDescriberTest {
    private final Model expected;
    private final DataSource dataset;

    public AnnotationDescriberTest() {
        String prologue = "@base <http://example.com/> . @prefix ex: <http://example.com/ns#> . @prefix anno: <http://www.w3.org/2000/10/annotation-ns#> . ";
        String anno1data = "<ah> anno:annotates <1> ; anno:body <ab> . <ab> ex:val <v1> . ";
        String anno2data = "<bh> anno:annotates <2> ; anno:body <bb> . <bb> ex:val <v2> . ";
        
        Model model = modelFrom(prologue, anno1data, anno2data);
        expected = modelFrom(prologue, anno1data);
        dataset = DataSourceImpl.createMem();
        dataset.addNamedModel("http://example.com/graph/1", model);
    }
    
    private Model modelFrom(String... lines) {
        StringBuilder sb = new StringBuilder();
        for (String s: lines) sb.append(s);
        Model model = ModelFactory.createDefaultModel();
        StringReader r = new StringReader(sb.toString());
        model.read(r, null, "TTL");
        return model;
    }
    
    private Model describe(Resource res) {
        Model model = ModelFactory.createDefaultModel();
        DescribeHandler dh = new AnnotationDescriber();
        Context ctxt = new Context();
        ctxt.put(ARQConstants.sysCurrentDataset, dataset);
        dh.start(model, ctxt);
        dh.describe(res);
        return model;
    }
    
    @Test
    public void testDescribe() {
        DescribeHandler dh = new AnnotationDescriber();
        Model result;
        
        result = describe(ResourceFactory.createResource("http://thing.invalid"));
        assertEquals("No hits for non-existent thing", 0, result.size());
        
        result = describe(ResourceFactory.createResource("http://example.com/1"));
        assertTrue(expected.isIsomorphicWith(result));
        
        result = describe(ResourceFactory.createResource("http://example.com/v1"));
        assertTrue(expected.isIsomorphicWith(result));
        
        result = describe(ResourceFactory.createResource("http://example.com/ah"));
        assertTrue(expected.isIsomorphicWith(result));
        
        result = describe(ResourceFactory.createResource("http://example.com/ab"));
        assertTrue(expected.isIsomorphicWith(result));
        
    }
    
}