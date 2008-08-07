package org.caboto.rest.resources;

import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderServlet;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 */
public class AbstractResourceTest extends TestCase {


    protected String springConfig;
    protected String _resourcePackages = "org.caboto.rest.resources";
    private final int port = 9090;
    private final String servletPath = "/caboto";

    private Server server;


    @Before
    public void setUp() {

        startJetty(port, servletPath);
    }

    @After
    public void tearDown() {
        stopJetty();
    }


    private void startJetty(int port, String servletPath) {

        try {
            server = new Server(port);
            final Context context = new Context(server, "/", Context.SESSIONS);

            final Map<String, String> contextParams = new HashMap<String, String>();
            contextParams.put("contextConfigLocation", "classpath:caboto-jaxrs-test-resources.xml");
            context.setInitParams(contextParams);


            final ServletHolder springServletHolder = new ServletHolder(ContextLoaderServlet.class);

            springServletHolder.setInitOrder(1);
            context.addServlet(springServletHolder, "/*");


            final ServletHolder servletHolder = new ServletHolder(SpringServlet.class);
            servletHolder.setInitParameter("com.sun.jersey.config.property.resourceConfigClass",
                    PackagesResourceConfig.class.getName());
            servletHolder.setInitParameter(PackagesResourceConfig.PROPERTY_PACKAGES,
                    _resourcePackages);
            servletHolder.setInitOrder(2);
            context.addServlet(servletHolder, servletPath + "/*");


           
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void stopJetty() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}