package org.caboto.security.spring;

import org.caboto.CabotoUtility;
import org.caboto.security.GateKeeper;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;

import java.util.regex.Pattern;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class GateKeeperImpl implements GateKeeper {

    public GateKeeperImpl(final String ADMIN_ROLE) {
        this.ADMIN_ROLE = ADMIN_ROLE;
    }

    public boolean userHasPermissionFor(final Object user, final Permission permission,
                                        final String resource) {

        // the user object should be a Spring Authentication
        Authentication authentication = (Authentication) user;

        // get the user name
        String username = getUsername(authentication);

        // ---------- PUBLIC resources

        // is it a public graph/uri?
        if (CabotoUtility.isPublicResource(resource)) {

            // anyone can read a public resource
            if (permission.equals(GateKeeper.Permission.READ)) {
                return true;
            }

            // you can add or delete if you are the owner or the admin
            if (permission.equals(GateKeeper.Permission.WRITE) ||
                    permission.equals(GateKeeper.Permission.DELETE)) {
                if (CabotoUtility.extractUsername(resource).equals(username) ||
                        inRole(ADMIN_ROLE, authentication.getAuthorities())) {
                    return true;
                }
            }

            return false;
        }

        // ---------- PRIVATE resources

        // is it a private resource? only owners and admins have access for any operation.
        if (CabotoUtility.isPrivateResource(resource)) {
            return CabotoUtility.extractUsername(resource).equals(username) ||
                    inRole(ADMIN_ROLE, authentication.getAuthorities());
        }


        // ---------- Secure Resource -> not sure this should be defined in this class?
        if (securedPattern.matcher(resource).find()) {

            System.out.println("Matched a secure resource!");

            return inRole(ADMIN_ROLE, authentication.getAuthorities()) ||
                    inRole("USER", authentication.getAuthorities());
        }


        // ---------- OTHER resources (no restrictions specified)

        return true;
    }

    /**
     * Helper method to extract the username from the Authentication object provided by Spring.
     *
     * @see <http://static.springframework.org/spring-security/site/reference/html/technical-overview.html#d4e605>
     * @param authentication object representing the authenticated user.
     * @return username of the authenticated user.
     */
    private String getUsername(final Authentication authentication) {

        String username;
        Object obj = authentication.getPrincipal();
        if (obj instanceof UserDetails) {
            username = ((UserDetails) obj).getUsername();
        } else {
            username = obj.toString();
        }

        return username;
    }

    /**
     * Helper method to determine if a user is in a specified role.
     *
     * @param role the role that we are interested in.
     * @param authorities the list of Spring Security authorities the user owns.
     * @return true if the user is in the specified role.
     */
    private boolean inRole(final String role, final GrantedAuthority[] authorities) {
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(role)) {
                return true;
            }
        }
        return false;
    }

    private final String ADMIN_ROLE;

    private static Pattern securedPattern = Pattern.compile("^.*/secured/.*$");
}
