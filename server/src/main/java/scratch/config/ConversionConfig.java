package scratch.config;

import java.util.HashSet;
import java.util.Set;

import com.github.pagehelper.Page;
import org.springframework.context.annotation.Configuration;
import scratch.support.service.PageBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.converter.Converter;

import scratch.model.entity.AnimeEpisode;
import scratch.model.entity.AnimeEpisodeScratch;


/**
 * 配置了类型转换器
 */
@Configuration
public class ConversionConfig {

	/**
	 * 类型转换
	 * @return
	 */
	@Bean
	public ConversionServiceFactoryBean conversionService() {
		ConversionServiceFactoryBean conversionServiceFactoryBean = new ConversionServiceFactoryBean();
		
		@SuppressWarnings("rawtypes")
		Set<Converter> converts = new HashSet<Converter>();

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

		converts.add(new Converter<Page, PageBean>() {

			@Override
			public PageBean convert(Page source) {
				scratch.support.service.Page page =
						new scratch.support.service.Page(source);
				return new PageBean(source, page);
			}
		});
		
		conversionServiceFactoryBean.setConverters(converts);
		return conversionServiceFactoryBean;
	}
	
}
