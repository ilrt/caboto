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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.store.StoreFormatter;
import junit.framework.TestCase;
import org.caboto.domain.Annotation;
import org.caboto.profile.MockProfileRepositoryImpl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 *
 **/
public class AnnotationDaoImplTest extends TestCase {


    public void setUp() {
        String storePath = this.getClass().getResource("/sdb.ttl").getPath();
        store = SDBFactory.connectStore(storePath);
        StoreFormatter storeFormatter = store.getTableFormatter();
        storeFormatter.format();
        annotationDao = new AnnotationDaoImpl(store, new MockProfileRepositoryImpl());
    }

    public void testAddAnnotation() {

        Annotation annotation = new Annotation();
        annotation.setGraphId("http://caboto.org/person/MikeJ/public/");
        annotation.setAnnotates("http://chillyinside.com/blog/?p=47");
        annotation.setAuthor("http://caboto.org/person/MikeJ/");
        annotation.setType("SimpleComment");

        Map<String, String> map = new HashMap<String, String>();
        map.put("title", "A title to the annotation");
        map.put("description", "Some description of the annotation");

        annotation.setBody(map);

        try {
            annotationDao.addAnnotation(annotation);

            assertTrue("There should be an id", annotation.getId() != null);
            assertTrue("There should be a creation data", annotation.getCreated() != null);

            Model model = SDBFactory.connectNamedModel(store, annotation.getGraphId());

            assertTrue("There should be statements in the model", !model.isEmpty());

        } catch (AnnotationDaoException e) {
            e.printStackTrace();
        }

    }

    public void testFindAnnotation() {

        try {

            // add some test data to the model
            InputStream is = this.getClass().getResourceAsStream("/test-comment.rdf");
            Model model = SDBFactory.connectNamedModel(store, "http://caboto.org/person/MikeJ/public/");
            model.read(is, null);

            Model m = annotationDao.findAnnotation("http://caboto.org/person/MikeJ/public/" +
                    "2474362d-c1eb-4a80-b971-c883e73d8406");


            assertNotNull("The model from the construct is null", m);
            assertEquals("Unexpected model size returned from the construct", model.size(),
                    m.size());

        } catch (AnnotationDaoException e) {
            e.printStackTrace();
        } 
    }


    private Store store;
    private AnnotationDao annotationDao;
}
