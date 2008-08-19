package org.caboto;

import javax.ws.rs.core.MediaType;

public class RdfMediaType {

    private RdfMediaType() {
    }

    public final static String APPLICATION_RDF_XML = "application/rdf+xml";

    public final static MediaType APPLICATION_RDF_XML_TYPE =
            new MediaType("application", "rdf+xml");

    public final static String TEXT_RDF_N3 = "text/rdf+n3";

    public final static MediaType TEXT_RDF_N3_TYPE = new MediaType("text", "rdf+n3");
}
