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

        System.out.println("I HAVE NO IDEA!");

        return ACCESS_ABSTAIN;
    }

    final private GateKeeper gateKeeper;

    private Pattern annotationContextPattern;
}
