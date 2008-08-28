package org.caboto.security.spring;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import org.caboto.CabotoUtility;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;

import java.lang.reflect.Method;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class ExitGuard implements AfterReturningAdvice {

    public ExitGuard(String ADMIN_ROLE) {
        this.ADMIN_ROLE = ADMIN_ROLE;
    }

    public void afterReturning(Object returnValue, Method method, Object[] args, Object target)
            throws Throwable {


        // only interested in the find annotations method
        if (method.getName().equals("findAnnotations")) {

            Model results = (Model) returnValue;

            // iterate over the resources
            ResIterator resIterator = results.listSubjects();

            while (resIterator.hasNext()) {

                Resource resource = resIterator.nextResource();

                if (CabotoUtility.isPrivateResource(resource.getURI())) {

                    Authentication authentication =
                            SecurityContextHolder.getContext().getAuthentication();

                    String username;
                    Object obj = authentication.getPrincipal();
                    if (obj instanceof UserDetails) {
                        username = ((UserDetails) obj).getUsername();
                    } else {
                        username = obj.toString();
                    }

                    if (!(username.equals(CabotoUtility.extractUsername(resource.getURI())) ||
                            inRole(ADMIN_ROLE, authentication.getAuthorities()))) {

                        results.remove(resource.listProperties());

                    }
                }
            }


        }


    }

    private boolean inRole(final String role, final GrantedAuthority[] authorities) {
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(role)) {
                return true;
            }
        }
        return false;
    }

    private String ADMIN_ROLE;

}
