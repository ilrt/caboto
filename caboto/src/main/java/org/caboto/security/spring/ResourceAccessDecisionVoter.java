package org.caboto.security.spring;

import org.caboto.CabotoUtility;
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.intercept.web.FilterInvocation;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.vote.AccessDecisionVoter;

import javax.servlet.http.HttpServletRequest;

public class ResourceAccessDecisionVoter implements AccessDecisionVoter {

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

        // the admin role can do what it likes...
        if (inRole("ROLE_ADMIN", authentication.getAuthorities())) {
            return ACCESS_GRANTED;
        }

        // get the username

        String username;
        Object obj = authentication.getPrincipal();
        if (obj instanceof UserDetails) {
            username = ((UserDetails) obj).getUsername();
        } else {
            username = obj.toString();
        }

        // is it a public resource?
        if (CabotoUtility.isPublicResource(request.getPathInfo())) {

            // restrictions on POST and DELETE
            if (request.getMethod().equalsIgnoreCase("POST") ||
                    request.getMethod().equalsIgnoreCase("DELETE")) {

                // check the username matches the path
                if (username.equals(CabotoUtility.extractUsername(request.getPathInfo()))) {
                    return ACCESS_GRANTED;
                } else {
                    return ACCESS_DENIED;
                }

            } else {
                return ACCESS_GRANTED;
            }

        }

        return ACCESS_ABSTAIN;
    }

    private boolean inRole(final String role, final GrantedAuthority[] authorities) {
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(role)) {
                return true;
            }
        }
        return false;
    }


}
