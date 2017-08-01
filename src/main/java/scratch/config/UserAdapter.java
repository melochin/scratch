package scratch.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import scratch.model.User;

public class UserAdapter implements UserDetails {

	private User user;
	
	public UserAdapter() {}
	
	public UserAdapter(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> authoritys = new ArrayList<SimpleGrantedAuthority>();
		if(new Integer(1).equals(this.user.getRole())) {
			authoritys.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}
		authoritys.add(new SimpleGrantedAuthority("ROLE_USER"));
		return authoritys;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}


}
