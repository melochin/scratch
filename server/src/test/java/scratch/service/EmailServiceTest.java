package scratch.service;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.model.entity.Anime;
import scratch.model.entity.AnimeEpisode;
import scratch.support.service.EmailSupport;
import scratch.support.service.MailException;
import scratch.test.ContextTest;

/**
 * 邮件服务测试
 * @author melochin
 * @since 2017.08.18
 */
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
		episode.setNumber("1");
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

	@Autowired
	private EmailSupport emailSupport;

	@Test
	public void test() throws MailException, MessagingException {
		String destEmail = "651356331@qq.com";

		for(int i=0; i<500; i++) {
			String[] message = "明 天 来 我 办 公 室 一 下 , 你 个 秃 头".split(" ");
			for(String m : message) {
				emailSupport.sendMail(m, m, destEmail);
			}
		}


	}

}
