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

import scratch.model.AnimeEpisode;
import scratch.model.AnimeEpisodeScratch;
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
		
		converts.add(new Converter<AnimeEpisodeScratch, AnimeEpisode>() {

			@Override
			public AnimeEpisode convert(AnimeEpisodeScratch episodeScratch) {
				AnimeEpisode animeEpisode = new AnimeEpisode();
				animeEpisode.setHostId(episodeScratch.getHostId());
				animeEpisode.setAnime(episodeScratch.getAnime());
				animeEpisode.setNumber(episodeScratch.getNumber());
				animeEpisode.setUrl(episodeScratch.getUrl());
				animeEpisode.setScratchTime(episodeScratch.getScratchTime());
				return animeEpisode;
			}
			
		});
		
		converts.add(new Converter<AnimeEpisode,AnimeEpisodeScratch>() {

			@Override
			public AnimeEpisodeScratch convert(AnimeEpisode episode) {
				AnimeEpisodeScratch scratch = new AnimeEpisodeScratch();
				scratch.setHostId(episode.getHostId());
				scratch.setAnime(episode.getAnime());
				scratch.setNumber(episode.getNumber());
				scratch.setUrl(episode.getUrl());
				scratch.setScratchTime(episode.getScratchTime());
				scratch.setStatus(0);
				return scratch;
			}
			
		});
		
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
