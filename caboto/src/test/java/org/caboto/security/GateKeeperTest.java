package org.caboto.security;

import junit.framework.TestCase;
import org.caboto.security.spring.GateKeeperImpl;
import org.junit.Before;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.providers.TestingAuthenticationToken;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class GateKeeperTest extends TestCase {

    @Before
    public void setUp() {
        gateKeeper = new GateKeeperImpl("ADMIN");
    }

    public void testPublicResourcesRead() {

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testUserOne,
                GateKeeper.Permission.READ, publicUriOne));

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testAdmin,
                GateKeeper.Permission.READ, publicUriOne));

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testUserTwo,
                GateKeeper.Permission.READ, publicUriOne));
    }

    public void testPublicResourcesWrite() {

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testUserOne,
                GateKeeper.Permission.WRITE, publicUriOne));

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testAdmin,
                GateKeeper.Permission.WRITE, publicUriOne));

        assertFalse("Access should not be granted.", gateKeeper.userHasPermissionFor(testUserTwo,
                GateKeeper.Permission.WRITE, publicUriOne));
    }

    public void testPublicResourcesDelete() {

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testUserOne,
                GateKeeper.Permission.DELETE, publicUriOne));

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testAdmin,
                GateKeeper.Permission.DELETE, publicUriOne));

        assertFalse("Access should not be granted.", gateKeeper.userHasPermissionFor(testUserTwo,
                GateKeeper.Permission.DELETE, publicUriOne));
    }

    public void testPrivateResourcesRead() {

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testUserOne,
                GateKeeper.Permission.READ, privateUriOne));

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testAdmin,
                GateKeeper.Permission.READ, privateUriOne));

        assertFalse("Access should not be granted.", gateKeeper.userHasPermissionFor(testUserTwo,
                GateKeeper.Permission.READ, privateUriOne));
    }

    public void testPrivateResourcesWrite() {

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testUserOne,
                GateKeeper.Permission.WRITE, privateUriOne));

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testAdmin,
                GateKeeper.Permission.WRITE, privateUriOne));

        assertFalse("Access should not be granted.", gateKeeper.userHasPermissionFor(testUserTwo,
                GateKeeper.Permission.WRITE, privateUriOne));
    }

    public void testPrivateResourcesDelete() {

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testUserOne,
                GateKeeper.Permission.DELETE, privateUriOne));

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testAdmin,
                GateKeeper.Permission.DELETE, privateUriOne));

        assertFalse("Access should not be granted.", gateKeeper.userHasPermissionFor(testUserTwo,
                GateKeeper.Permission.DELETE, privateUriOne));
    }

    // some test users

    private final Authentication testUserOne =
            new TestingAuthenticationToken("mike", "secret",
                    new GrantedAuthority[]{new GrantedAuthorityImpl("USERS")});

    private final Authentication testUserTwo =
            new TestingAuthenticationToken("damian", "secret",
                    new GrantedAuthority[]{new GrantedAuthorityImpl("USERS")});

    private final Authentication testAdmin =
            new TestingAuthenticationToken("admin", "dunno",
                    new GrantedAuthority[]{new GrantedAuthorityImpl("ADMIN")});


    // some test uris

    private final String publicUriOne =
            "http://caboto.org/annotations/person/mike/public/bf3b3796-030d-4c2d-a23b-47bf6db060b0";

    private final String privateUriOne =
            "http://caboto.org/annotations/person/mike/private/bf3b3796-030d-4c2d-a23b-47bf6db00b0";

    private GateKeeper gateKeeper;
}
