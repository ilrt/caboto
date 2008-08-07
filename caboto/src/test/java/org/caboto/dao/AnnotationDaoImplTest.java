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
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.SDBFactory;
import junit.framework.TestCase;
import org.apache.commons.configuration.ConfigurationException;
import org.caboto.domain.Annotation;
import org.caboto.profile.MockProfileRepositoryImpl;
import org.caboto.store.StoreFactory;
import org.caboto.store.StoreFactoryDefaultImpl;
import org.caboto.store.StoreInitializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: AnnotationDaoImplTest.java 183 2008-05-30 14:24:23Z mike.a.jones $
 */
public class AnnotationDaoImplTest extends TestCase {

    @Before
    public void setUp() throws ConfigurationException {

        // initialize the store
        StoreInitializer storeInitializer = new StoreInitializer(formatConfigFile,
                formatPropertyKey, sdbConfigFile);
        storeInitializer.initializeStore();

        storeFactory = new StoreFactoryDefaultImpl(sdbConfigFile);
        store = storeFactory.create();

        annotationDao = new AnnotationDaoImpl(new MockProfileRepositoryImpl(), storeFactory);
    }

    @After
    public void tearDown() throws IOException {

        // ensure that the formatter configuration is reset after each test, i.e "true" is
        // replaced with "false" ... this ensures that the store is reformatted at the start
        // of each test and thus deletes data that was left after the previous test.

        String fullPath = getClass().getResource(formatConfigFile).getPath();
        Properties props = new Properties();
        props.load(new FileInputStream(new File(fullPath)));
        props.setProperty(formatPropertyKey, "false");
        props.store(new FileOutputStream(new File(fullPath)), null);

        storeFactory.destroy(store);

    }

    @Test
    public void testAddAnnotation() {

        Annotation annotation = new Annotation();
        annotation.setGraphId("http://caboto.org/person/mikej/public/");
        annotation.setAnnotates("http://chillyinside.com/blog/?p=47");
        annotation.setAuthor("http://caboto.org/person/mikej/");
        annotation.setType("SimpleComment");

        Map<String, String> map = new HashMap<String, String>();
        map.put("title", "A title to the annotation");
        map.put("description", "Some description of the annotation");

        annotation.setBody(map);


        annotationDao.addAnnotation(annotation);

        assertTrue("There should be an id", annotation.getId() != null);
        assertTrue("There should be a creation data", annotation.getCreated() != null);

        Model model = SDBFactory.connectDataset(store).getNamedModel(annotation.getGraphId());

        assertTrue("There should be statements in the model", !model.isEmpty());

    }

    @Test
    public void testFindAnnotation() {

        // add some test data to the model
        InputStream is = this.getClass().getResourceAsStream("/test-graph1.rdf");
        Model model = SDBFactory.connectDataset(store)
                .getNamedModel("http://caboto.org/person/mikej/public/");
        model.read(is, null);

        Resource r = annotationDao.findAnnotation("http://caboto.org/person/mikej/public/" +
                "e609962d-47ee-4248-9fcc-2c9f6256c330");

        assertNotNull("The resource from the construct is null", r);
        assertEquals("Unexpected model size returned from the construct", 7,
                r.getModel().size());
    }

    @Test
    public void testFindAnnotations() {

        SDBFactory.connectDataset(store).getNamedModel("http://caboto.org/person/mikej/public/")
                .read(this.getClass().getResourceAsStream("/test-graph1.rdf"), null);

        SDBFactory.connectDataset(store).getNamedModel("http://caboto.org/person/mikel/public/")
                .read(this.getClass().getResourceAsStream("/test-graph2.rdf"), null);

        Model m = annotationDao.findAnnotations("http://chillyinside.com/blog/?p=45");

        assertEquals("Unexpected size", 14, m.size());


    }

    @Test
    public void testDeleteAnnotation() {


        Model m = SDBFactory.connectDataset(store)
                .getNamedModel("http://caboto.org/person/mikej/public/")
                .read(this.getClass().getResourceAsStream("/test-graph1.rdf"), null);

        assertEquals("The start size is incorrect", 14, m.size());

        Resource resource = annotationDao.findAnnotation("http://caboto.org/person/mikej/" +
                "public/e609962d-47ee-4248-9fcc-2c9f6256c330");

        assertTrue("Unable to find resorce to delete", !resource.getModel().isEmpty());

        annotationDao.deleteAnnotation(resource);

        assertEquals("The resource was not deleted", 7, m.size());


    }

    private AnnotationDao annotationDao;
    private final String sdbConfigFile = "/sdb.ttl";
    private final String formatConfigFile = "/startup.properties";
    private final String formatPropertyKey = "sdb.store.formatted";
    private StoreFactory storeFactory;
    private Store store;
}
