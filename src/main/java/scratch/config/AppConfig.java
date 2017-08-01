package scratch.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import scratch.model.User;
import scratch.model.UserForm;
import scratch.service.UserService;

public class AppConfig {

	@Autowired
	private UserService userService;
	
	@Bean
	public ConversionServiceFactoryBean conversionService() {
		ConversionServiceFactoryBean conversionServiceFactoryBean = new ConversionServiceFactoryBean();
		
		Converter<UserForm, User> convert = new Converter<UserForm, User>() {

			@Override
			public User convert(UserForm source) {
				User user = new User();
				user.setUserId(source.getUserId());
				user.setPassword(source.getNewPassword());
				return user;
			}
		};
		
		@SuppressWarnings("rawtypes")
		Set<Converter> converts = new HashSet<Converter>();
		converts.add(convert);
		
		conversionServiceFactoryBean.setConverters(converts);
		return conversionServiceFactoryBean;
	}
	
	@Bean
	public UserDetailsService userDetailService() {
		return new UserDetailsService() {
			
			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				User user = userService.getByName(username);
				UserAdapter userInfo = new UserAdapter(user);
				return userInfo;
			}
		};
	}
}
