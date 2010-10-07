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
package org.caboto.rest.resources;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.JDBC;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import org.caboto.dao.AnnotationDao;
import org.caboto.dao.AnnotationDaoImpl;
import org.caboto.domain.Annotation;
import org.caboto.jena.db.Database;
import org.caboto.jena.db.impl.SDBDatabase;
import org.caboto.profile.ProfileRepository;
import org.caboto.profile.ProfileRepositoryException;
import org.caboto.profile.ProfileRepositoryXmlImpl;
import org.caboto.rest.providers.JenaModelRdfProvider;
import org.caboto.rest.providers.JenaResourceRdfProvider;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public abstract class AbstractResourceTest {

    // ---------- Helper methods for starting and stopping jetty

    void startJetty() {

        try {
            server = configureJetty();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void startJettyWithSecurity() {

        try {
            server = configureJettyWithSecurity();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void stopJetty() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------- Helper methods for configuring jersey

    Server configureJetty() {

        Server server = new Server(PORT_NUMBER);
        final Context context = new Context(server, "/", Context.SESSIONS);
        configureSpringContext(context);
        configureJersey(context);

        return server;
    }

    Server configureJettyWithSecurity() {

        Server server = configureJetty();
        Context context = (Context) server.getHandler();
        configureSpringSecurity(context);
        return server;
    }

    // ---------- Jetty configuration methods

    private void configureSpringContext(final Context context) {

        // set context patameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("contextConfigLocation", SPRING_CONTEXT);
        context.setInitParams(params);

        // establish the Context loader servlet
        //ServletHolder contextServletHolder = new ServletHolder(ContextLoader.class);

        EventListener springContextListener = new ContextLoaderListener();
        context.addEventListener(springContextListener);

//        contextServletHolder.setInitOrder(1);
//        context.addServlet(contextServletHolder, "/*");

    }

    private void configureJersey(final Context context) {

        // establish servlet
        ServletHolder springServletHolder = new ServletHolder(SpringServlet.class);

        // set the initialization parameters
        springServletHolder.setInitParameter(JERSEY_RESOURCE_CONFIG_CLASS,
                PackagesResourceConfig.class.getName());
        springServletHolder.setInitParameter(PackagesResourceConfig.PROPERTY_PACKAGES,
                CABOTO_PACKAGE_RESOURCES);
        springServletHolder.setInitOrder(2);
        context.addServlet(springServletHolder, SERVLET_PATH + "/annotation/*");
    }


    private void configureSpringSecurity(final Context context) {

        // set context patameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("contextConfigLocation", SPRING_CONTEXT + "," + SPRING_SECURITY_CONTEXT);
        context.setInitParams(params);

        // establish the security filter
        FilterHolder filterHolder =
                new FilterHolder(org.springframework.web.filter.DelegatingFilterProxy.class);
        filterHolder.setName("springSecurityFilterChain"); // spring will moan without this
        context.addFilter(filterHolder, "/*", org.mortbay.jetty.Handler.DEFAULT);
    }

    // ---------- Helper methods for the RESTful clients

    ClientResponse createPostClientResponse(String username, String password,
                                            String uri, String type, String postData) {

        Client c = Client.create();

        if (username != null && password != null) {
            c.addFilter(new BasicAuthenticationClientFilter(username, password));
        }

        return c.resource(uri).type(type).post(ClientResponse.class, postData);
    }

    ClientResponse createGetClientResponse(String username, String password,
                                           String uri, String type) {

        Client c = Client.create(createClientConfig());

        if (username != null && password != null) {
            c.addFilter(new BasicAuthenticationClientFilter(username, password));
        }

        return c.resource(uri).accept(type).get(ClientResponse.class);
    }

    ClientConfig createClientConfig() {

        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(JenaResourceRdfProvider.class);
        config.getClasses().add(JenaModelRdfProvider.class);
        return config;
    }

    // ---------- Helper methods for handling the store and creating test data

    void formatDataStore() {
        try {
            Model ttl = ModelFactory.createDefaultModel();
            ttl.read(getClass().getResourceAsStream("/sdb.ttl"), null, "TTL");
            StoreDesc storeDesc = StoreDesc.read(ttl);
            String driver = JDBC.getDriver(storeDesc.getDbType());
            JDBC.loadDriver(driver);
            Connection sqlConn = DriverManager.getConnection(
                    storeDesc.connDesc.getJdbcURL(),
                    storeDesc.connDesc.getUser(),
                    storeDesc.connDesc.getPassword());
            Store store = SDBFactory.connectStore(sqlConn, storeDesc);
            store.getTableFormatter().format();
            store.getTableFormatter().truncate();
            store.close();
            sqlConn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String createAndSaveAnnotation(String graphUri) throws ProfileRepositoryException {
        Annotation annotation = createTestAnnotation(graphUri);
        saveAnnotation(annotation);
        return annotation.getId();
    }

    Annotation createTestAnnotation(String graphUri) {

        // body of the annotation
        Map<String, List<String>> body = new HashMap<String, List<String>>();
        body.put("title", new ArrayList<String>());
        body.get("title").add("A title");
        body.put("description", new ArrayList<String>());
        body.get("description").add("A description");

        // main bits of the annotation
        Annotation annotation = new Annotation();
        annotation.setAnnotates(annotated);
        annotation.setType("SimpleComment");
        annotation.setGraphId(graphUri);
        annotation.setBody(body);

        return annotation;
    }

    void saveAnnotation(Annotation annotation) throws ProfileRepositoryException {

        try {
            Database database = new SDBDatabase("/sdb.ttl");
            ProfileRepository profileRepository = new ProfileRepositoryXmlImpl("test-profiles.xml");
            AnnotationDao annotationDao = new AnnotationDaoImpl(profileRepository,
                    database);

            annotationDao.addAnnotation(annotation);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ProfileRepositoryException(e.getMessage());
        }

    }


    // ---------- Jetty server configuration

    private Server server;

    final private int PORT_NUMBER = 9090;

    // ---------- Jersey configuration

    final private String JERSEY_RESOURCE_CONFIG_CLASS =
            "com.sun.jersey.config.property.resourceConfigClass";

    final private String CABOTO_PACKAGE_RESOURCES = "org.caboto.rest";

    private final String SERVLET_PATH = "/caboto";

    // ---------- Spring configuration files

    final private String SPRING_CONTEXT = "classpath:caboto-context.xml";

    final private String SPRING_SECURITY_CONTEXT = "classpath:caboto-security.xml";

    // ---------- URIs and Data used accross tests


    final String baseUri = "http://localhost:9090/caboto/annotation/";

    private final String userPublicPathOne = "person/mike/public/";

    private final String userPublicPathTwo = "person/damian/public/";

    private final String userPrivatePathOne = "person/mike/private/";

    private final String userPrivatePathTwo = "person/damian/private/";

    String userPublicUriOne = baseUri + userPublicPathOne;

    String userPublicUriTwo = baseUri + userPublicPathTwo;

    String userPrivateUriOne = baseUri + userPrivatePathOne;

    String userPrivateUriTwo = baseUri + userPrivatePathTwo;

    final String annotated = "http://caboto.org/somethinginteresting";

    String validPostData = "title=A%20Title&description=A%20description&type=" +
            "SimpleComment&annotates=http%3A%2F%2Fexample.org%2Fthing";

    String garbagePostData = "aaabbbcccdddeeefffggghhhiii";

    // ---------- Some test credentials

    final String usernameOne = "mike";
    final String passwordOne = "potato";

}
