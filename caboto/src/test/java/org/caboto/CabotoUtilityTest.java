package org.caboto;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class CabotoUtilityTest extends TestCase {

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

        assertFalse("Should be a private resource",
                CabotoUtility.isPublicResource(PRIVATE_URI_ONE));
        assertFalse("Should be a private resource",
                CabotoUtility.isPublicResource(PRIVATE_URI_TWO));
        assertFalse("Should be a private resource",
                CabotoUtility.isPublicResource(PRIVATE_URI_THREE));
        assertFalse("Should be a private resource",
                CabotoUtility.isPublicResource(PRIVATE_URI_FOUR));

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

        assertFalse("Should be a public resource",
                CabotoUtility.isPrivateResource(PUBLIC_URI_ONE));
        assertFalse("Should be a public resource",
                CabotoUtility.isPrivateResource(PUBLIC_URI_TWO));
        assertFalse("Should be a public resource",
                CabotoUtility.isPrivateResource(PUBLIC_URI_THREE));
        assertFalse("Should be a public resource",
                CabotoUtility.isPrivateResource(PUBLIC_URI_FOUR));

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
    }

    
    private final String PUBLIC_URI_ONE = "/person/mike/public/";
    private final String PUBLIC_URI_TWO =
            "/person/mike/public/bcd6dbe7-fbd4-44a9-816e-57697720f2b9";
    private final String PUBLIC_URI_THREE = "/person/damian/public/";
    private final String PUBLIC_URI_FOUR =
            "/person/damian/public/bcd6dbe7-fbd4-44a9-816e-57697720f2b9";
    private final String PRIVATE_URI_ONE = "/person/mike/private/";
    private final String PRIVATE_URI_TWO =
            "/person/mike/private/bcd6dbe7-fbd4-44a9-816e-57697720f2b9";
    private final String PRIVATE_URI_THREE = "/person/damian/private/";
    private final String PRIVATE_URI_FOUR =
            "/person/damian/private/bcd6dbe7-fbd4-44a9-816e-57697720f2b9";
    private final String USER_ONE = "mike";
    private final String USER_TWO = "damian";
}
