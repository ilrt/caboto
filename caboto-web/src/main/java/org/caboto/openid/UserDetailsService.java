package org.caboto.openid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.User;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UsernameNotFoundException;

public class UserDetailsService implements
		org.springframework.security.userdetails.UserDetailsService {
	
	final static Log log = LogFactory.getLog(UserDetailsService.class);
	final static GrantedAuthority auth = new GrantedAuthorityImpl("ROLE_USER");
	
	public UserDetails loadUserByUsername(String arg0)
			throws UsernameNotFoundException, DataAccessException {
		log.info("Knock knock: " + arg0);
		log.info("Authority: " + auth);
		return new User(arg0, "not needed", true, true, true, true, new GrantedAuthority[] {auth});
	}

}
