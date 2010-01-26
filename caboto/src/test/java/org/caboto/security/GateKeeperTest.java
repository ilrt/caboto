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
package org.caboto.security;

import org.caboto.security.spring.GateKeeperImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class GateKeeperTest {

    @Before
    public void setUp() {
        gateKeeper = new GateKeeperImpl("ADMIN");
    }

    @Test
    public void testPublicResourcesRead() {

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testUserOne,
                GateKeeper.Permission.READ, publicUriOne));

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testAdmin,
                GateKeeper.Permission.READ, publicUriOne));

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testUserTwo,
                GateKeeper.Permission.READ, publicUriOne));
    }

    @Test
    public void testPublicResourcesWrite() {

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testUserOne,
                GateKeeper.Permission.WRITE, publicUriOne));

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testAdmin,
                GateKeeper.Permission.WRITE, publicUriOne));

        assertFalse("Access should not be granted.", gateKeeper.userHasPermissionFor(testUserTwo,
                GateKeeper.Permission.WRITE, publicUriOne));
    }

    @Test
    public void testPublicResourcesDelete() {

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testUserOne,
                GateKeeper.Permission.DELETE, publicUriOne));

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testAdmin,
                GateKeeper.Permission.DELETE, publicUriOne));

        assertFalse("Access should not be granted.", gateKeeper.userHasPermissionFor(testUserTwo,
                GateKeeper.Permission.DELETE, publicUriOne));
    }

    @Test
    public void testPrivateResourcesRead() {

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testUserOne,
                GateKeeper.Permission.READ, privateUriOne));

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testAdmin,
                GateKeeper.Permission.READ, privateUriOne));

        assertFalse("Access should not be granted.", gateKeeper.userHasPermissionFor(testUserTwo,
                GateKeeper.Permission.READ, privateUriOne));
    }

    @Test
    public void testPrivateResourcesWrite() {

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testUserOne,
                GateKeeper.Permission.WRITE, privateUriOne));

        assertTrue("Access should be granted.", gateKeeper.userHasPermissionFor(testAdmin,
                GateKeeper.Permission.WRITE, privateUriOne));

        assertFalse("Access should not be granted.", gateKeeper.userHasPermissionFor(testUserTwo,
                GateKeeper.Permission.WRITE, privateUriOne));
    }

    @Test
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
