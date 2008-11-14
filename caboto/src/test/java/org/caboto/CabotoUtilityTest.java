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

import junit.framework.TestCase;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class CabotoUtilityTest extends TestCase {

    @Test
    public void testGenerateUuid() {

        String url = CabotoUtility.generateId(PUBLIC_URI_ONE);
        String testString = url.subSequence(PUBLIC_URI_ONE.length(), url.length()).toString();
        Pattern p = Pattern.compile(UUID_PATTERN);

        assertTrue("Unexpected UUID pattern", p.matcher(testString).find());
    }

    @Test
    public void testParseDate() throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Date date = sdf.parse(ORIGINAL_DATE);

        assertEquals("Incorrect date.", PARSED_DATE, CabotoUtility.parseDate(date));
    }

    @Test
    public void testPublicResource() {

        assertTrue("Should be a public resource",
                CabotoUtility.isPublicResource(PUBLIC_URI_ONE));
        assertTrue("Should be a public resource",
                CabotoUtility.isPublicResource(PUBLIC_URI_TWO));
        assertTrue("Should be a public resource",
                CabotoUtility.isPublicResource(PUBLIC_URI_THREE));
        assertTrue("Should be a public resource",
                CabotoUtility.isPublicResource(PUBLIC_URI_FOUR));
        assertTrue("Should be a public resource",
                CabotoUtility.isPublicResource(PUBLIC_URI_FIVE));
        assertTrue("Should be a public resource",
                CabotoUtility.isPublicResource(PUBLIC_URI_SIX));
        assertTrue("Should be a public resource",
                CabotoUtility.isPublicResource(PUBLIC_URI_SEVEN));
        assertTrue("Should be a public resource",
                CabotoUtility.isPublicResource(PUBLIC_URI_EIGHT));
        assertTrue("Should be a public resource",
                CabotoUtility.isPublicResource(PUBLIC_URI_NINE));
        assertTrue("Should be a public resource",
                CabotoUtility.isPublicResource(PUBLIC_URI_TEN));


        assertFalse("Should be a private resource",
                CabotoUtility.isPublicResource(PRIVATE_URI_ONE));
        assertFalse("Should be a private resource",
                CabotoUtility.isPublicResource(PRIVATE_URI_TWO));
        assertFalse("Should be a private resource",
                CabotoUtility.isPublicResource(PRIVATE_URI_THREE));
        assertFalse("Should be a private resource",
                CabotoUtility.isPublicResource(PRIVATE_URI_FOUR));
        assertFalse("Should be a private resource",
                CabotoUtility.isPublicResource(PRIVATE_URI_FIVE));
        assertFalse("Should be a private resource",
                CabotoUtility.isPublicResource(PRIVATE_URI_SIX));
        assertFalse("Should be a private resource",
                CabotoUtility.isPublicResource(PRIVATE_URI_SEVEN));
        assertFalse("Should be a private resource",
                CabotoUtility.isPublicResource(PRIVATE_URI_EIGHT));
        assertFalse("Should be a private resource",
                CabotoUtility.isPublicResource(PRIVATE_URI_NINE));
        assertFalse("Should be a private resource",
                CabotoUtility.isPublicResource(PRIVATE_URI_TEN));

    }

    @Test
    public void testPrivateResource() {

        assertTrue("Should be a private resource",
                CabotoUtility.isPrivateResource(PRIVATE_URI_ONE));
        assertTrue("Should be a private resource",
                CabotoUtility.isPrivateResource(PRIVATE_URI_TWO));
        assertTrue("Should be a private resource",
                CabotoUtility.isPrivateResource(PRIVATE_URI_THREE));
        assertTrue("Should be a private resource",
                CabotoUtility.isPrivateResource(PRIVATE_URI_FOUR));
        assertTrue("Should be a private resource",
                CabotoUtility.isPrivateResource(PRIVATE_URI_FIVE));
        assertTrue("Should be a private resource",
                CabotoUtility.isPrivateResource(PRIVATE_URI_SIX));
        assertTrue("Should be a private resource",
                CabotoUtility.isPrivateResource(PRIVATE_URI_SEVEN));
        assertTrue("Should be a private resource",
                CabotoUtility.isPrivateResource(PRIVATE_URI_EIGHT));
        assertTrue("Should be a private resource",
                CabotoUtility.isPrivateResource(PRIVATE_URI_NINE));
        assertTrue("Should be a private resource",
                CabotoUtility.isPrivateResource(PRIVATE_URI_TEN));

        assertFalse("Should be a public resource",
                CabotoUtility.isPrivateResource(PUBLIC_URI_ONE));
        assertFalse("Should be a public resource",
                CabotoUtility.isPrivateResource(PUBLIC_URI_TWO));
        assertFalse("Should be a public resource",
                CabotoUtility.isPrivateResource(PUBLIC_URI_THREE));
        assertFalse("Should be a public resource",
                CabotoUtility.isPrivateResource(PUBLIC_URI_FOUR));
        assertFalse("Should be a public resource",
                CabotoUtility.isPrivateResource(PUBLIC_URI_FIVE));
        assertFalse("Should be a public resource",
                CabotoUtility.isPrivateResource(PUBLIC_URI_SIX));
        assertFalse("Should be a public resource",
                CabotoUtility.isPrivateResource(PUBLIC_URI_SEVEN));
        assertFalse("Should be a public resource",
                CabotoUtility.isPrivateResource(PUBLIC_URI_EIGHT));
        assertFalse("Should be a public resource",
                CabotoUtility.isPrivateResource(PUBLIC_URI_NINE));
        assertFalse("Should be a public resource",
                CabotoUtility.isPrivateResource(PUBLIC_URI_TEN));
    }

    @Test
    public void testExtractUsername() {

        assertEquals("Incorrect name", USER_ONE, CabotoUtility.extractUsername(PUBLIC_URI_ONE));
        assertEquals("Incorrect name", USER_ONE, CabotoUtility.extractUsername(PUBLIC_URI_TWO));
        assertEquals("Incorrect name", USER_ONE, CabotoUtility.extractUsername(PRIVATE_URI_ONE));
        assertEquals("Incorrect name", USER_ONE, CabotoUtility.extractUsername(PRIVATE_URI_TWO));

        assertEquals("Incorrect name", USER_TWO, CabotoUtility.extractUsername(PUBLIC_URI_THREE));
        assertEquals("Incorrect name", USER_TWO, CabotoUtility.extractUsername(PUBLIC_URI_FOUR));
        assertEquals("Incorrect name", USER_TWO, CabotoUtility.extractUsername(PRIVATE_URI_THREE));
        assertEquals("Incorrect name", USER_TWO, CabotoUtility.extractUsername(PRIVATE_URI_FOUR));

        assertEquals("Incorrect name", USER_THREE, CabotoUtility.extractUsername(PUBLIC_URI_FIVE));
        assertEquals("Incorrect name", USER_THREE, CabotoUtility.extractUsername(PUBLIC_URI_SIX));
        assertEquals("Incorrect name", USER_THREE, CabotoUtility.extractUsername(PRIVATE_URI_FIVE));
        assertEquals("Incorrect name", USER_THREE, CabotoUtility.extractUsername(PRIVATE_URI_SIX));

        assertEquals("Incorrect name", USER_FOUR, CabotoUtility.extractUsername(PUBLIC_URI_SEVEN));
        assertEquals("Incorrect name", USER_FOUR, CabotoUtility.extractUsername(PUBLIC_URI_EIGHT));
        assertEquals("Incorrect name", USER_FOUR, CabotoUtility.extractUsername(PRIVATE_URI_SEVEN));
        assertEquals("Incorrect name", USER_FOUR, CabotoUtility.extractUsername(PRIVATE_URI_EIGHT));

        assertEquals("Incorrect name", USER_FIVE, CabotoUtility.extractUsername(PUBLIC_URI_NINE));
        assertEquals("Incorrect name", USER_FIVE, CabotoUtility.extractUsername(PUBLIC_URI_TEN));
        assertEquals("Incorrect name", USER_FIVE, CabotoUtility.extractUsername(PRIVATE_URI_NINE));
        assertEquals("Incorrect name", USER_FIVE, CabotoUtility.extractUsername(PRIVATE_URI_TEN));
	
	// openid
        assertEquals("Incorrect name", USER_SIX, CabotoUtility.extractUsername(PUBLIC_URI_ELEVEN));
        assertEquals("Incorrect name", USER_SIX, CabotoUtility.extractUsername(PRIVATE_URI_ELEVEN));
    }

    @Test
    public void testPublicGraph() {

        assertTrue("Should be a public annotation", CabotoUtility.isPublicGraph(PUBLIC_GRAPH_ONE));
        assertFalse("should be an incorrect public graph",
                CabotoUtility.isPublicGraph(PUBLIC_GRAPH_TWO));
        assertFalse("Should be an incorrect public graph",
                CabotoUtility.isPublicGraph(PUBLIC_GRAPH_THREE));
        assertFalse("Should be an incorrect public graph",
                CabotoUtility.isPublicGraph(PUBLIC_GRAPH_FOUR));

    }

    @Test
    public void testPrivateGraph() {

        assertTrue("Should be a private annotation",
                CabotoUtility.isPrivateGraph(PRIVATE_GRAPH_ONE));
        assertFalse("should be an incorrect private graph",
                CabotoUtility.isPrivateGraph(PRIVATE_GRAPH_TWO));
        assertFalse("Should be an incorrect private graph",
                CabotoUtility.isPrivateGraph(PRIVATE_GRAPH_THREE));
        assertFalse("Should be an incorrect private graph",
                CabotoUtility.isPrivateGraph(PRIVATE_GRAPH_FOUR));

    }


    // public URIs
    private final String PUBLIC_URI_ONE = "/person/mike/public/";
    private final String PUBLIC_URI_TWO =
            "/person/mike/public/bcd6dbe7-fbd4-44a9-816e-57697720f2b9";
    private final String PUBLIC_URI_THREE = "/person/damian/public/";
    private final String PUBLIC_URI_FOUR =
            "/person/damian/public/bcd6dbe7-fbd4-44a9-816e-57697720f2b9";
    private final String PUBLIC_URI_FIVE =
            "/annotations/person/jasper/public/";
    private final String PUBLIC_URI_SIX =
            "/annotations/person/jasper/public/bcd6dbe7-fbd4-44a9-816e-57697720f2b9";
    private final String PUBLIC_URI_SEVEN =
            "/annotations/person/gérard/public/";
    private final String PUBLIC_URI_EIGHT =
            "/annotations/person/gérard/public/bcd6dbe7-fbd4-44a9-816e-57697720f2b9";
    private final String PUBLIC_URI_NINE =
            "/annotations/person/???/public/bcd6dbe7-fbd4-44a9-816e-57697720f2b9";
    private final String PUBLIC_URI_TEN =
            "/annotations/person/???/public/bcd6dbe7-fbd4-44a9-816e-57697720f2b9";
    private final String PUBLIC_URI_ELEVEN =
            "/annotations/person/http://someone.openid.org/id//public/bcd6dbe7-fbd4-44a9-816e-57697720f2b9";

    // private URIs
    private final String PRIVATE_URI_ONE = "/person/mike/private/";
    private final String PRIVATE_URI_TWO =
            "/person/mike/private/bcd6dbe7-fbd4-44a9-816e-57697720f2b9";
    private final String PRIVATE_URI_THREE = "/person/damian/private/";
    private final String PRIVATE_URI_FOUR =
            "/person/damian/private/bcd6dbe7-fbd4-44a9-816e-57697720f2b9";
    private final String PRIVATE_URI_FIVE =
            "/annotations/person/jasper/private/";
    private final String PRIVATE_URI_SIX =
            "/annotations/person/jasper/private/bcd6dbe7-fbd4-44a9-816e-57697720f2b9";
    private final String PRIVATE_URI_SEVEN =
            "/annotations/person/gérard/private/bcd6dbe7-fbd4-44a9-816e-57697720f2b9";
    private final String PRIVATE_URI_EIGHT =
            "/annotations/person/gérard/private/bcd6dbe7-fbd4-44a9-816e-57697720f2b9";
    private final String PRIVATE_URI_NINE =
            "/annotations/person/???/private/bcd6dbe7-fbd4-44a9-816e-57697720f2b9";
    private final String PRIVATE_URI_TEN =
            "/annotations/person/???/private/bcd6dbe7-fbd4-44a9-816e-57697720f2b9";
    private final String PRIVATE_URI_ELEVEN =
            "/annotations/person/http://someone.openid.org/id//private/bcd6dbe7-fbd4-44a9-816e-57697720f2b9";


    // public graphs
    private final String PUBLIC_GRAPH_ONE = "http://caboto.org/caboto/person/mike/public/";
    private final String PUBLIC_GRAPH_TWO = "http://caboto.org/caboto/person/mike/public/aaaaaaaa";
    private final String PUBLIC_GRAPH_THREE =
            "http://caboto.org/caboto/person/mike/private/public/";
    private final String PUBLIC_GRAPH_FOUR =
            "http://caboto.org/caboto/person/mike/public/public/public/";

    // private graphs
    private final String PRIVATE_GRAPH_ONE = "http://caboto.org/caboto/person/mike/private/";
    private final String PRIVATE_GRAPH_TWO =
            "http://caboto.org/caboto/person/mike/private/aaaaaaaa";
    private final String PRIVATE_GRAPH_THREE =
            "http://caboto.org/caboto/person/mike/public/private/";
    private final String PRIVATE_GRAPH_FOUR =
            "http://caboto.org/caboto/person/mike/private/private/private/";

    // users
    private final String USER_ONE = "mike";
    private final String USER_TWO = "damian";
    private final String USER_THREE = "jasper";
    private final String USER_FOUR = "gérard";
    private final String USER_FIVE = "???";
    private final String USER_SIX = "http://someone.openid.org/id/";

    // date formats and dates
    private final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    private final String ORIGINAL_DATE = "2008-08-15T12:12:00+0100";
    private final String PARSED_DATE = "2008-08-15T12:12:00+01:00";

    // expected regex pattern for a UUID
    private final String UUID_PATTERN = "^\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}$";

}
