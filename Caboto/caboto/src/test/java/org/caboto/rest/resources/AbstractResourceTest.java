package org.caboto.rest.resources;

import com.hp.hpl.jena.sdb.Store;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import junit.framework.TestCase;
import org.caboto.dao.AnnotationDao;
import org.caboto.dao.AnnotationDaoImpl;
import org.caboto.domain.Annotation;
import org.caboto.profile.ProfileRepository;
import org.caboto.profile.ProfileRepositoryException;
import org.caboto.profile.ProfileRepositoryXmlImpl;
import org.caboto.rest.providers.JenaModelRdfProvider;
import org.caboto.rest.providers.JenaResourceRdfProvider;
import org.caboto.store.StoreFactory;
import org.caboto.store.StoreFactoryDefaultImpl;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderServlet;

import java.util.HashMap;
import java.util.Map;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public abstract class AbstractResourceTest extends TestCase {


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
        ServletHolder contextServletHolder = new ServletHolder(ContextLoaderServlet.class);
        contextServletHolder.setInitOrder(1);
        context.addServlet(contextServletHolder, "/*");

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
        context.addServlet(springServletHolder, SERVLET_PATH + "/*");
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

    ClientResponse createPostClientResponse(String uri, String type, String postData) {

        Client c = Client.create();
        return c.resource(uri).type(type).post(ClientResponse.class, postData);
    }

    ClientResponse createGetClientResponse(String uri, String type) {

        Client c = Client.create(createClientConfig());
        return c.resource(uri).accept(type).get(ClientResponse.class);
    }

    ClientConfig createClientConfig() {

        ClientConfig config = new DefaultClientConfig();
        config.getProviderClasses().add(JenaResourceRdfProvider.class);
        config.getProviderClasses().add(JenaModelRdfProvider.class);
        return config;
    }


    // ---------- Helper methods for handling the store and creating test data

    void formatDataStore() {

        StoreFactory storeFactory = new StoreFactoryDefaultImpl("/sdb.ttl");
        Store store = storeFactory.create();
        store.getTableFormatter().format();
    }

    String createAndSaveAnnotation() throws ProfileRepositoryException {
        Annotation annotation = createTestAnnotation();
        saveAnnotation(annotation);
        return annotation.getId();
    }

    Annotation createTestAnnotation() {

        // body of the annotation
        Map<String, String> body = new HashMap<String, String>();
        body.put("title", "A title");
        body.put("description", "A description");

        // main bits of the annotation
        Annotation annotation = new Annotation();
        annotation.setAnnotates(annotated);
        annotation.setType("SimpleComment");
        annotation.setGraphId(userUriOne);
        annotation.setBody(body);

        return annotation;
    }

    void saveAnnotation(Annotation annotation) throws ProfileRepositoryException {

        StoreFactory storeFactory = new StoreFactoryDefaultImpl("/sdb.ttl");

        ProfileRepository profileRepository = new ProfileRepositoryXmlImpl("test-profiles.xml");
        AnnotationDao annotationDao = new AnnotationDaoImpl(profileRepository, storeFactory);

        annotationDao.addAnnotation(annotation);

    }


    // ---------- Handling credentials in the client

    void setCredentials(final String username, final String password) {
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.toCharArray());
            }
        });
    }

    void clearCredentials() {
        Authenticator.setDefault(null);
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


    String baseUri = "http://localhost:9090/caboto/";

    String userPublicUriOne = "person/mike/public/";

    String userPublicUriTwo = "person/damian/public/";

    String userUriOne = baseUri + userPublicUriOne;

    String userUriTwo = baseUri + userPublicUriTwo;

    String annotated = "http://caboto.org/somethinginteresting";

    String validPostData = "title=A%20Title&description=A%20description&type=" +
            "SimpleComment&annotates=http%3A%2F%2Fexample.org%2Fthing";


    // ---------- Some test credentials

    final String usernameOne = "mike";
    final String passwordOne = "cheese";

}