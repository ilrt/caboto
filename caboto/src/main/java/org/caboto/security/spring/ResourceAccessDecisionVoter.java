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
package org.caboto.security.spring;

import org.caboto.security.GateKeeper;
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.intercept.web.FilterInvocation;
import org.springframework.security.vote.AccessDecisionVoter;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class ResourceAccessDecisionVoter implements AccessDecisionVoter {

    public ResourceAccessDecisionVoter(GateKeeper gateKeeper, String context) {
        this.gateKeeper = gateKeeper;
        annotationContextPattern = Pattern.compile("^.*/" + context + "/.*$");
    }

    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    public boolean supports(Class aClass) {
        return (FilterInvocation.class.isAssignableFrom(aClass));
    }

    public int vote(Authentication authentication, Object o,
                    ConfigAttributeDefinition configAttributeDefinition) {

        HttpServletRequest request =
                ((FilterInvocation) o).getHttpRequest();

        String method = request.getMethod();

        String path = request.getRequestURI();

        if (path == null) {
            return ACCESS_ABSTAIN;
        }

        if(annotationContextPattern.matcher(path).find()) {

            if (method.equalsIgnoreCase("GET")) {
                if (gateKeeper.userHasPermissionFor(authentication, GateKeeper.Permission.READ,
                        path)) {
                    return ACCESS_GRANTED;
                }
            }

            if (method.equalsIgnoreCase("POST")) {
                if (gateKeeper.userHasPermissionFor(authentication, GateKeeper.Permission.WRITE,
                        path)) {
                    return ACCESS_GRANTED;
                }
            }

            if (method.equalsIgnoreCase("DELETE")) {
                if (gateKeeper.userHasPermissionFor(authentication, GateKeeper.Permission.DELETE,
                        path)) {
                    return ACCESS_GRANTED;
                }
            }

            return ACCESS_DENIED;
        }

        return ACCESS_ABSTAIN;
    }

    private final GateKeeper gateKeeper;

    private final Pattern annotationContextPattern;
}
