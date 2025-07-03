
package com.letmecall.rgt.config;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditorAwareImpl implements AuditorAware<String> {
	public static final Logger logger = LoggerFactory.getLogger(AuditorAwareImpl.class);

	@Override
	public Optional<String> getCurrentAuditor() {
		return Optional.of(getCurrentAuditor(SecurityContextHolder.getContext()));
	}

	public String getCurrentAuditor(SecurityContext securityContext) {
//		//logger.info("getCurrentAuditor ::: securityContext={},authentication={}", securityContext,
//				securityContext.getAuthentication());
		String user = "SYSTEM";
		if (securityContext.getAuthentication() != null && securityContext.getAuthentication().isAuthenticated()) {
			user = securityContext.getAuthentication().getPrincipal().toString();
		}
		//logger.info("Logged in user information : user={}", user);
		return user;

	}
}
