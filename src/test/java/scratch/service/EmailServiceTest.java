package scratch.service;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.model.Anime;
import scratch.model.AnimeEpisode;
import scratch.support.service.MailException;
import scratch.test.ContextTest;

public class EmailServiceTest extends ContextTest {

	@Autowired
	private EmailService service;
	
	private static final String DESTEMAIL = "398299262@qq.com";
	
	private static final String URL = "https://www.google.com";
	
	@Test
	public void snedAnimeInfoTest() throws MailException, MessagingException {
		List<AnimeEpisode> episodes = new ArrayList<AnimeEpisode>();
		AnimeEpisode episode = new AnimeEpisode();
		episode.setUrl(URL);
		episode.setNumber(1);
		episode.setAnime(new Anime("1"));
		episodes.add(episode);
		service.sendAnimeInfo(episodes, DESTEMAIL);
	}
	
	@Test
	public void sendUserResetPasswordTest() throws MailException, MessagingException {
		service.sendUserResetPassword(URL, DESTEMAIL);
	}
	
	@Test
	public void sendUserActiveCodeTest() throws MailException, MessagingException {
		service.sendUserActiveCode(URL, DESTEMAIL);
	}

}
