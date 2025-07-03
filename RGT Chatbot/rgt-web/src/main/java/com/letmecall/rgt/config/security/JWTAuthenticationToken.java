package com.letmecall.rgt.config.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.letmecall.rgt.model.TokenData;

/**
 * @author Subbareddy
 * 
 *         Customized token for REST User security
 *
 */
public class JWTAuthenticationToken extends AbstractAuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3338252872866341012L;
	private final Object principal;
	private final Collection<GrantedAuthority> authorities;

	/**
	 * @param authorities
	 */
	public JWTAuthenticationToken(TokenData tokenData, Collection<GrantedAuthority> authorities) {
		super(null);
		super.setAuthenticated(true);
		this.principal = tokenData;
		this.authorities = authorities;
	}

	@Override
	public Object getCredentials() {
		return "";
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
