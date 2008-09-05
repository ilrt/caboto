package org.caboto.domain;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class VersionDetails {

    public VersionDetails(String cabotoVersion) {
        this.cabotoVersion = cabotoVersion;
    }

    public String getCabotoVersion() {
        return cabotoVersion;
    }

    public void setCabotoVersion(String cabotoVersion) {
        this.cabotoVersion = cabotoVersion;
    }

    private String cabotoVersion;
}
