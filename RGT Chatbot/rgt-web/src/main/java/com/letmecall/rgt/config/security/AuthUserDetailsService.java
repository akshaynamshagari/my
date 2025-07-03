package com.letmecall.rgt.config.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Service
public interface AuthUserDetailsService {
	
	UserDetails loadUserByUsername(String username);
}
