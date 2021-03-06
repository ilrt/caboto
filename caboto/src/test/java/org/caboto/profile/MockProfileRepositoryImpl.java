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
package org.caboto.profile;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: MockProfileRepositoryImpl.java 123 2008-05-19 14:32:58Z mike.a.jones $
 */
public class MockProfileRepositoryImpl implements ProfileRepository {

    public Profile findProfile(String profileId) throws ProfileRepositoryException {

        if (profileId.equals("SimpleComment")) {

            Profile profile = new Profile();
            profile.setId("SimpleComment");
            profile.setType("http://caboto.org/schema/annotations#SimpleComment");

            ProfileEntry entry1 = new ProfileEntry();
            entry1.setId("title");
            entry1.setPropertyType("http://purl.org/dc/elements/1.1/title");
            entry1.setObjectDatatype("String");
            entry1.setRequired(true);

            ProfileEntry entry2 = new ProfileEntry();
            entry2.setId("description");
            entry2.setPropertyType("http://purl.org/dc/elements/1.1/description");
            entry2.setObjectDatatype("String");
            entry2.setRequired(true);

            profile.getProfileEntries().add(entry1);
            profile.getProfileEntries().add(entry2);

            return profile;
        }

        return null;
    }

    public Profile findProfileByUri(String profileUri)
            throws ProfileRepositoryException {

        if (profileUri.equals("http://caboto.org/schema/annotations#SimpleComment")) {

            Profile profile = new Profile();
            profile.setId("SimpleComment");
            profile.setType("http://caboto.org/schema/annotations#SimpleComment");

            ProfileEntry entry1 = new ProfileEntry();
            entry1.setId("title");
            entry1.setPropertyType("http://purl.org/dc/elements/1.1/title");
            entry1.setObjectDatatype("String");
            entry1.setRequired(true);

            ProfileEntry entry2 = new ProfileEntry();
            entry2.setId("description");
            entry2.setPropertyType("http://purl.org/dc/elements/1.1/description");
            entry2.setObjectDatatype("String");
            entry2.setRequired(true);

            profile.getProfileEntries().add(entry1);
            profile.getProfileEntries().add(entry2);

            return profile;
        }

        return null;
    }

}
