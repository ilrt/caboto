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

    private final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    private final String ORIGINAL_DATE = "2008-08-15T12:12:00+0100";
    private final String PARSED_DATE = "2008-08-15T12:12:00+01:00";

    private final String UUID_PATTERN = "^\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}$";

}
