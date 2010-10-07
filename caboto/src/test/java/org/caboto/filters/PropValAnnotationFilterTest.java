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

package org.caboto.filters;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;

/**
 *
 * @author pldms
 */
public class PropValAnnotationFilterTest {

    public PropValAnnotationFilterTest() {
    }
    
    /**
     * Check factory creates prop val filters from params correctly
     */
    @Test
    public final void checkPropValFactory() {
    	Map<String,List<String>> params = 
    		Collections.singletonMap("x:prop", Collections.singletonList("foo"));
    	AnnotationFilter[] filters = AnnotationFilterFactory.getFromParameters(params);
    	assertEquals("Got a filter", 1, filters.length);
    	assertTrue("Got right kind of thing", PropValAnnotationFilter.class.isInstance(filters[0]));
    	assertEquals("x:prop", ((PropValAnnotationFilter) filters[0]).propertyS);
    	assertEquals(Node.createLiteral("foo", null, XSDDatatype.XSDstring), ((PropValAnnotationFilter) filters[0]).value);
    }
    
    /**
     * Check factory creates larq filters from params correctly
     */
    @Test
    public final void checkLarqFactory() {
    	Map<String,List<String>> params = 
    		Collections.singletonMap("search", Collections.singletonList("foo"));
    	AnnotationFilter[] filters = AnnotationFilterFactory.getFromParameters(params);
    	assertEquals("Got a filter", 1, filters.length);
    	assertTrue("Got right kind of thing", LarqAnnotationFilter.class.isInstance(filters[0]));
    	assertEquals("foo", ((LarqAnnotationFilter) filters[0]).searchTerm);
    }
    
    /**
     * Test of visitQueryPattern method, of class PropValAnnotationFilter.
     */
    @Test
    public final void testAugmentQuery() {
        Query query = QueryFactory.create("PREFIX x: <http://ex.com/> "
                + "SELECT * { GRAPH ?g { ?s ?p ?o }}");
        PropValAnnotationFilter filter =
                new PropValAnnotationFilter("x:prop", "bar");
        Query toChange = query.cloneQuery();
        filter.augmentQuery(toChange, "s");
        Query query2 = QueryFactory.create("PREFIX x: <http://ex.com/> "
                + "SELECT * { GRAPH ?g { ?s ?p ?o ; x:prop \"bar\"^^<http://www.w3.org/2001/XMLSchema#string> }}");
        assertEquals(query2, toChange);

        filter = new PropValAnnotationFilter("x:prop", "U:http://ex.com/z");
        toChange = query.cloneQuery();
        filter.augmentQuery(toChange, "s");
        query2 = QueryFactory.create("PREFIX x: <http://ex.com/> "
                + "SELECT * { GRAPH ?g { ?s ?p ?o ; x:prop <http://ex.com/z> }}");
        assertEquals(query2, toChange);
    }
    
    /**
     * Check the larq basics
     */
    @Test
    public final void testLarqQuery() {
    	LarqAnnotationFilter filter;
    	try {
    		filter = new LarqAnnotationFilter("evil! \" . }");
    		fail("Should have thrown illegal argument");
    	} catch (IllegalArgumentException e) {}
    	
    	filter = new LarqAnnotationFilter("foo");
    	Query query = QueryFactory.create("PREFIX x: <http://ex.com/> "
                + "SELECT * { GRAPH ?g { ?s ?p ?o }}");
    	Query toChange = query.cloneQuery();
    	filter.augmentQuery(toChange, "s");
    	Query query2 = QueryFactory.create("PREFIX x: <http://ex.com/> "
                + "SELECT * { GRAPH ?g { ?s ?p ?o ; <http://jena.hpl.hp.com/ARQ/property#textMatch> \"foo\" }}");
    	assertEquals(query2, toChange);
    }

}