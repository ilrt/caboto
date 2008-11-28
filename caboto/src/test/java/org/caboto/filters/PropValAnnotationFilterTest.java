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

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pldms
 */
public class PropValAnnotationFilterTest {

    public PropValAnnotationFilterTest() {
    }

    /**
     * Test of visitQueryPattern method, of class PropValAnnotationFilter.
     * TODO Test factory!
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

}