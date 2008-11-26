/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.caboto.rest.resources;

import javax.ws.rs.core.Response;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pldms
 */
public class AboutResourceTest extends AbstractResourceTest {
    @Before
    @Override
    public void setUp() {
        formatDataStore();
        startJetty();
    }

    @After
    @Override
    public void tearDown() {
        stopJetty();
    }

    /**
     * Test of findAnnotations method, of class AboutResource.
     */
    @Test
    public void testFindAnnotations() throws Exception {
        String url = createAndSaveAnnotation(userPublicUriOne);
    }

}