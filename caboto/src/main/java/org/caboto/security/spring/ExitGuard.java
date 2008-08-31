package org.caboto.security.spring;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import org.caboto.security.GateKeeper;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

import java.lang.reflect.Method;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class ExitGuard implements AfterReturningAdvice {

    public ExitGuard(GateKeeper gateKeeper) {
        this.gateKeeper = gateKeeper;
    }

    public void afterReturning(Object returnValue, Method method, Object[] args, Object target)
            throws Throwable {

        // only interested in the find annotations method
        if (method.getName().equals("findAnnotations")) {

            Model results = (Model) returnValue;

            // iterate over the resources
            ResIterator resIterator = results.listSubjects();

            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            while (resIterator.hasNext()) {

                Resource resource = resIterator.nextResource();

                if (!gateKeeper.userHasPermissionFor(authentication, GateKeeper.Permission.READ,
                        resource.getURI())) {
                    results.remove(resource.listProperties());
                }

            }
        }
    }


    private final GateKeeper gateKeeper;

}
