/*
 * Copyright (c) 2008, University of Bristol
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
 * 3) Neither the name of the University of Bristol nor the names of its
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * <p>A generic utility class with static methods that are used across a number of classes.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: CabotoUtility.java 177 2008-05-30 13:50:59Z mike.a.jones $
 */
public final class CabotoUtility {


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

    /**
     * <p>Parses a date to a format that is a valid XSD:dateTime.</p>
     *
     * @param date the date object to be parsed.
     * @return the date represented as a valid XSD:dateTime.
     */
    public static String parseDate(final Date date) {

        String temp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(date);

        return temp.substring(0, temp.length() - 2) + ":"
                + temp.substring(temp.length() - 2, temp.length());
    }


}
