package org.caboto.profile;

/**
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 *
 **/
public interface ProfileRepository {

    Profile findProfile(String profileId) throws ProfileRepositoryException;

}
