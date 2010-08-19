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
package org.caboto;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * <p>A generic utility class with static methods that are used across a number of classes.</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: CabotoUtility.java 177 2008-05-30 13:50:59Z mike.a.jones $
 */
public final class CabotoUtility {

	private static String DATE_FORMAT_STRING="yyyy-MM-dd'T'HH:mm:ssZ";
	
    /**
     * Private constructor.
     */
    private CabotoUtility() {
    }

    /**
     * <p>Generates a unique URI based on a UUID. The base of the URI needs to be provided.</p>
     *
     * @param graphId the base of the URI.
     * @return a unique URI based on a UUID.
     */
    public static String generateId(final String graphId) {

        String id = UUID.randomUUID().toString();

        if (!graphId.endsWith("/")) {
            id = "/" + id;
        }

        return graphId + id;
    }
    
    
    public static String getGraphId(final String id) {

    	int index=id.lastIndexOf("/");
    	if (index!=-1) {
    		return id.substring(0,index+1);
    		
    	}
        return null;
    }
    
    public static String getId(final String uri) {

    	int index=uri.lastIndexOf("/");
    	if (index!=-1) {
    		return uri.substring(index+1);
    	}
        return null;
    }

    /**
     * <p>Parses a date to a format that is a valid XSD:dateTime.</p>
     *
     * @param date the date object to be parsed.
     * @return the date represented as a valid XSD:dateTime.
     */
    public static String parseDate(final Date date) {

        String temp = new SimpleDateFormat(DATE_FORMAT_STRING).format(date);

        return temp.substring(0, temp.length() - 2) + ":"
                + temp.substring(temp.length() - 2, temp.length());
    }

    public static Date parseDate (final String XsdDate) throws ParseException{
    	String temp = XsdDate.substring(0, XsdDate.length() - 3)
        + XsdDate.substring(XsdDate.length() - 2, XsdDate.length());

    	Date date = new SimpleDateFormat(DATE_FORMAT_STRING).parse(temp);
    	return date;
    }
    
    
    /**
     * @param path the path details of a request.
     * @return whether or not its a public resource.
     */
    public static boolean isPublicResource(String path) {
        return publicResourcePattern.matcher(path).find();
    }

    /**
     * @param path the path details of a request.
     * @return whether or not its a private resource.
     */
    public static boolean isPrivateResource(String path) {
        return privateResourcePattern.matcher(path).find();
    }

    /**
     * @param graphUri the graph of an annotation.
     * @return whether or not its a public graph.
     */
    public static boolean isPublicGraph(String graphUri) {
        return publicGraphPattern.matcher(graphUri).find();
    }

    /**
     * @param graphUri the graph of an annotation.
     * @return whether or not its a private graph.
     */
    public static boolean isPrivateGraph(String graphUri) {
        return privateGraphPattern.matcher(graphUri).find();
    }


    /**
     * @param path the path details of a request.
     * @return the username that is part of the path.
     */
    public static String extractUsername(String path) {
	Matcher m = usernamePattern.matcher(path);
	if(m.matches()) {
            try {
                return URLDecoder.decode(m.group(1), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return m.group(1);
            }
	}
	// can't recover from this!
	throw new RuntimeException("No username found in the path.");
    }

    private static Pattern usernamePattern = Pattern.compile("^.*/person/(.*)/(public|private)/[^/]*$");

    private static Pattern publicResourcePattern = Pattern.compile("^.*/person/.*/public/.*$");
    private static Pattern privateResourcePattern = Pattern.compile("^.*/person/.*/private/.*$");

    private static Pattern publicGraphPattern = Pattern.compile("^.*/person/[^/]*/public/$");
    private static Pattern privateGraphPattern = Pattern.compile("^.*/person/[^/]*/private/$");


    private static String personPath = "/person/";

}
