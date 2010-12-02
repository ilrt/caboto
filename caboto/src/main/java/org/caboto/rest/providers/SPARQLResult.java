/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.caboto.rest.providers;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.caboto.RdfMediaType;
import org.caboto.jena.db.Results;

/**
 * Convert SELECT and ASK results to media types
 * @author pldms
 */
@Provider
@Produces({RdfMediaType.APPLICATION_SPARQL, RdfMediaType.APPLICATION_JSON, MediaType.WILDCARD})
public class SPARQLResult implements MessageBodyWriter<Object> {

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return (Results.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type));
    }

    public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        if (t instanceof Boolean) {
            if (RdfMediaType.APPLICATION_JSON_TYPE.equals(mediaType)) ResultSetFormatter.outputAsJSON(entityStream, (Boolean) t);
            else ResultSetFormatter.outputAsXML(entityStream, (Boolean) t);
        } else  { // Results
            try {
                ResultSet r = ((Results) t).getResults();
                if (RdfMediaType.APPLICATION_JSON_TYPE.equals(mediaType)) ResultSetFormatter.outputAsJSON(entityStream, r);
                else ResultSetFormatter.outputAsXML(entityStream, r);
            } finally { ((Results) t).close(); }
        }
    }

}
