package scratch.service.scratch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import scratch.config.RabbitMQConfig;
import scratch.dao.inter.IAnimeEpisodeDao;
import scratch.dao.inter.IAnimeFocusDao;
import scratch.dao.inter.IUserDao;
import scratch.model.entity.AnimeEpisode;
import scratch.model.entity.AnimeFocus;
import scratch.model.entity.User;

/**
 * 更新推送服务
 * 产生邮箱任务，发送给消息队列
 * 11.09 - 测试
 */
@Service
public class NotifyTaskProducer {

	private IAnimeFocusDao focusDao;

	private IAnimeEpisodeDao episodeDao;

	private IUserDao userDao;

	private RabbitTemplate rabbitTemplate;

	@Autowired
	public NotifyTaskProducer(IAnimeFocusDao focusDao,
							  IAnimeEpisodeDao episodeDao,
							  IUserDao userDao,
							  RabbitTemplate rabbitTemplate) {
		this.focusDao = focusDao;
		this.episodeDao = episodeDao;
		this.userDao = userDao;
		this.rabbitTemplate = rabbitTemplate;
	}

	public void push() {
		userDao.findAll()
				.forEach(this::notifyUser);
	}

	/**
	 * 通知用户更新的Anime
	 *
	 * @param user
	 */
	private void notifyUser(User user) {
		// 获取用户的关注列表
		List<AnimeFocus> animeFocusList = focusDao.listByUserId(user.getUserId());
		if (animeFocusList == null || animeFocusList.size() == 0) return;

		// 从关注列表中查找需要推送的Episode
		List<AnimeEpisode> episodeList = findNeedPushEpisode(animeFocusList);
		if (episodeList.size() == 0) return;

		// 向消息队列推送邮箱任务
		produce(user, episodeList, animeFocusList);
	}


	/**
	 * @param animeFocusList not null && size > 0
	 * @return
	 */
	private List<AnimeEpisode> findNeedPushEpisode(@NonNull List<AnimeFocus> animeFocusList) {
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

	/**
	 * 发送邮箱任务给消息队列
	 *
	 * @param user
	 * @param episodeList
	 * @param focusList
	 */
	private void produce(final User user,
						 final List<AnimeEpisode> episodeList,
						 final List<AnimeFocus> focusList) {

		Map<String, Object> message = new HashMap<String, Object>();
		message.put("email", user.getEmail());
		message.put("episodeList", episodeList);
		message.put("focusList", focusList);

		rabbitTemplate.convertAndSend(
				RabbitMQConfig.EXCHANGE_EMAIL,
				RabbitMQConfig.QUEUE_EMAIL_NOTIFY,
				message);
	}

}
