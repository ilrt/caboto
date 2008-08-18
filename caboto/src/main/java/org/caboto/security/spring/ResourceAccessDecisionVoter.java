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

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class ResourceAccessDecisionVoter implements AccessDecisionVoter {

    public ResourceAccessDecisionVoter(String ADMIN_ROLE) {
        this.ADMIN_ROLE = ADMIN_ROLE;
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

        String username;
        Object obj = authentication.getPrincipal();
        if (obj instanceof UserDetails) {
            username = ((UserDetails) obj).getUsername();
        } else {
            username = obj.toString();
        }


        // -------- "public" resources

        if (CabotoUtility.isPublicResource(request.getPathInfo())) {

            // POST and DELETE are a special case
            if (request.getMethod().equalsIgnoreCase("POST") ||
                    request.getMethod().equalsIgnoreCase("DELETE")) {

                // if the uid isn't in the path or an ADMIN user then deny access
                if (!(username.equals(CabotoUtility.extractUsername(request.getPathInfo())) ||
                        inRole(ADMIN_ROLE, authentication.getAuthorities()))) {

                    return ACCESS_DENIED;
                }
            }

            return ACCESS_GRANTED;
        }


        // -------- "private" resources

        if (CabotoUtility.isPrivateResource(request.getPathInfo())) {

            if (username.equals(CabotoUtility.extractUsername(request.getPathInfo())) ||
                    inRole(ADMIN_ROLE, authentication.getAuthorities())) {
                return ACCESS_GRANTED;
            }

            return ACCESS_DENIED;
        }


        // if we are here ... we have no opinion
        return ACCESS_ABSTAIN;
    }

    /**
     * A utility class to check if a user belongs to a specific role.
     *
     * @param role        the role that we are interested in.
     * @param authorities the list of authorities that the user owns.
     * @return does the user belong to the role?
     */

    private boolean inRole(final String role, final GrantedAuthority[] authorities) {
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Holds the value of an administrative role.
     */
    private final String ADMIN_ROLE;

}
