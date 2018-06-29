package scratch.service.anime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import scratch.dao.inter.IAnimeEpisodeDao;
import scratch.dao.inter.IAnimeFocusDao;
import scratch.dao.inter.IUserDao;
import scratch.model.entity.AnimeEpisode;
import scratch.model.entity.AnimeFocus;
import scratch.model.entity.User;
import scratch.service.EmailService;
import scratch.support.service.MailException;

@Service
public class AnimePushService {

	private final static Logger log = Logger.getLogger(AnimePushService.class);

	@Autowired
	private IAnimeFocusDao focusDao;

	@Autowired
	private IAnimeEpisodeDao episodeDao;

	@Autowired
	private IUserDao userDao;

	@Autowired
	private EmailService emailService;

	private static ExecutorService exec = Executors.newCachedThreadPool();

	@Scheduled(fixedRate = 30 * 60 * 1000)
	public void push() {

		if (log.isInfoEnabled()) {
			log.info("start push service");
		}

		//获取所有用户
		List<User> userList = userDao.findAll();
		userList.forEach(user -> {
			//获取用户关注的Anime列表
			List<AnimeFocus> animeFocusList = focusDao.listByUserId(user.getUserId());
			if (!hasFocusList(animeFocusList)) return;
			List<AnimeEpisode> episodeList = findNeedPushEpisode(animeFocusList);

			//发送推送邮件(如果没有Anime没有更新的话，不发送推送)
			if (episodeList.size() == 0) return;
			sendMail(user, episodeList, animeFocusList);
		});

		if (log.isInfoEnabled()) {
			log.info("end push service");
		}
	}

	private boolean hasFocusList(List<AnimeFocus> animeFocusList) {
		return animeFocusList != null && animeFocusList.size() > 0;
	}

	private List<AnimeEpisode> findNeedPushEpisode(List<AnimeFocus> animeFocusList) {
		List<AnimeEpisode> episodeList = new ArrayList<>();
		for (AnimeFocus focus : animeFocusList) {
			//根据Anime和时间，检索出应该推送给用户的Anime Episode
			List<AnimeEpisode> episodes = episodeDao.findByAnimeAndTime(focus.getAnime(), focus.getLastPushTime());
			//更新订阅的Anime的pushTime
			episodes.forEach(e -> {
				if (e.getScratchTime().getTime() <= focus.getLastPushTime().getTime()) return;
				focus.setLastPushTime(e.getScratchTime());
			});
			episodeList.addAll(episodes);
		}
		return episodeList;
	}


	private void sendMail(final User user, final List<AnimeEpisode> episodeList, final List<AnimeFocus> focusList) {

		exec.execute(() -> {

			try {
				emailService.sendAnimeInfo(episodeList, user.getEmail());
			} catch (MailException | MessagingException e) {
				e.printStackTrace();
			}
			;

			for (AnimeFocus focus : focusList) {
				focusDao.update(focus);
			}
		});
	}

}
