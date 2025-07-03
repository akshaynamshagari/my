package com.letmecall.rgt.config.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.letmecall.rgt.domain.UserAuthDetails;
import com.letmecall.rgt.utility.Constant.Role;
import com.letmecall.service.UserAuthDetailsServcie;

@Service
public class AuthUserDetailsServiceImpl implements AuthUserDetailsService {

	@Autowired
	private UserAuthDetailsServcie userAuthDetailsServcie;

	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		UserAuthDetails user = fetchUserAuthDetails(userName);
		if (user != null) {
			Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
			grantedAuthorities.add(new SimpleGrantedAuthority(Role.ROLE_USER.name()));
			for (String roleName : user.getRoles()) {
				grantedAuthorities.add(new SimpleGrantedAuthority(roleName));
			}

			return new User(userName, user.getPassword(), grantedAuthorities);
		} else {
			return null;
		}
	}

	UserAuthDetails fetchUserAuthDetails(String userName) {
		UserAuthDetails userAuthDetails = new UserAuthDetails();
		userAuthDetails.setEmailAddress(userName);
		return userAuthDetailsServcie.fetchCompleteUserAuthDetails(userAuthDetails);
	}

}
