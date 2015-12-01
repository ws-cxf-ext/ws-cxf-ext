package org.ws.cxf.ext.interceptor;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.ws.cxf.ext.Constants;

/**
 * SecurityContext stub for application which use not Spring Security.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 */
public class CustomSecurityContextStub implements SecurityContext {

	private static final long serialVersionUID = 1L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAuthentication(Authentication authentication) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Authentication getAuthentication() {
		return new AuthenticationStub();
	}

	/**
	 * User details stub.
	 * 
	 * @author Idriss Neumann <neumann.idriss@gmail.com>
	 *
	 */
	public static class UserDetailsStub implements UserDetails {

		private static final long serialVersionUID = 1L;

		private List<GrantedAuthority> authorities;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEnabled() {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isCredentialsNonExpired() {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isAccountNonLocked() {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isAccountNonExpired() {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getUsername() {
			return Constants.TECH_USER;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPassword() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return authorities;
		}

	}

	/**
	 * Authentification stub.
	 * 
	 * @author Idriss Neumann <neumann.idriss@gmail.com>
	 *
	 */
	public static class AuthenticationStub implements Authentication {

		private static final long serialVersionUID = 1L;

		private UserDetails principal = new UserDetailsStub();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return Constants.TECH_USER;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isAuthenticated() {
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object getPrincipal() {
			return principal;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object getDetails() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object getCredentials() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return principal.getAuthorities();
		}
	}
}
