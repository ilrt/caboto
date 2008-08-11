package org.caboto.rest.resources;

import com.hp.hpl.jena.sdb.Store;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import junit.framework.TestCase;
import org.caboto.store.StoreFactory;
import org.caboto.store.StoreFactoryDefaultImpl;
import org.caboto.rest.providers.JenaResourceRdfProvider;
import org.caboto.rest.providers.JenaModelRdfProvider;
import org.caboto.profile.ProfileRepositoryException;
import org.caboto.profile.ProfileRepository;
import org.caboto.profile.ProfileRepositoryXmlImpl;
import org.caboto.domain.Annotation;
import org.caboto.dao.AnnotationDao;
import org.caboto.dao.AnnotationDaoImpl;
import org.junit.After;
import org.junit.Before;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderServlet;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public abstract class AbstractResourceTest extends TestCase {


    protected String springConfig;
    protected String _resourcePackages = "org.caboto.rest";
    private final int port = 9090;
    private final String servletPath = "/caboto";

    private Server server;


    @Before
    public void setUp() {
        formatDataStore();
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

    void formatDataStore() {
        StoreFactory storeFactory = new StoreFactoryDefaultImpl("/sdb.ttl");
        Store store = storeFactory.create();
        store.getTableFormatter().format();
    }

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
        annotation.setGraphId(baseUri + userPublicUri);
        annotation.setBody(body);

        return annotation;
    }

    void saveAnnotation(Annotation annotation) throws ProfileRepositoryException {

        StoreFactory storeFactory = new StoreFactoryDefaultImpl("/sdb.ttl");

        ProfileRepository profileRepository = new ProfileRepositoryXmlImpl("test-profiles.xml");
        AnnotationDao annotationDao = new AnnotationDaoImpl(profileRepository, storeFactory);

        annotationDao.addAnnotation(annotation);

    }

    String baseUri = "http://localhost:9090/caboto/";

    String userPublicUri = "person/mike/public/";

    String annotated = "http://caboto.org/somethinginteresting";
}