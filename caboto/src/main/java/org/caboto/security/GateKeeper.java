package org.caboto.security;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public interface GateKeeper {

    boolean userHasPermissionFor(Object user, Permission permission, String resource);

    enum Permission { READ, WRITE, DELETE }

}
