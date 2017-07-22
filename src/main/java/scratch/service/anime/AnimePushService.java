package scratch.service.anime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import scratch.model.AnimeEpisode;
import scratch.model.AnimeFocus;
import scratch.model.User;
import scratch.service.MailTemplate;
import scratch.support.service.MailContent;
import scratch.support.service.MailException;
import scratch.support.service.MailService;

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
	private MailService mailService;
	
	private static ExecutorService exec = Executors.newCachedThreadPool();
	
	@Scheduled(fixedRate=30*60*1000)
	public void push() {
		if(log.isInfoEnabled()) {
			log.info("start push service");
		}
		
		//获取所有用户
		List<User> userList = userDao.findAll();
		for(User user : userList) {
			//获取用户关注的Anime列表
			List<AnimeFocus> animeFocusList = focusDao.findByUserId(user.getUserId());
			//用户如果没有关注的Anime，则跳过该用户
			if(animeFocusList == null || animeFocusList.size() == 0) continue;
			
			List<AnimeEpisode> episodeList = new ArrayList<AnimeEpisode>();
			for(AnimeFocus focus : animeFocusList) {
				//根据Anime和时间，检索出应该推送给用户的Anime Episode
				List<AnimeEpisode> episodes = episodeDao.findByAnimeAndTime(focus.getAnime(), focus.getLastPushTime());
				//更新订阅的Anime的pushTime
				for(AnimeEpisode e : episodes) {
					if(e.getScratchTime().getTime() > focus.getLastPushTime().getTime()) {
						focus.setLastPushTime(e.getScratchTime());
					}
				}
				episodeList.addAll(episodes);
			}
			//发送推送邮件(如果没有Anime没有更新的话，不发送推送)
			if(episodeList.size() == 0) continue;
			push(user, episodeList, animeFocusList);
		}
		
		if(log.isInfoEnabled()) {
			log.info("end push service");
		}
	}
	
	private void push(final User u, final List<AnimeEpisode> episodeList, final List<AnimeFocus> focusList) {
		
		exec.execute(new Runnable() {

			@Override
			public void run() {
				
				//获取用户邮箱
				String mail = u.getEmail();
				
				//构造发送内容
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("episodes", episodeList);
				
				try {
					mailService.sendMail(new MailContent("番剧推送", "/mail/animepush"), mail, map);
				} catch (MailException | MessagingException e) {
					e.printStackTrace();
				}
				
				for(AnimeFocus focus : focusList) {
					focusDao.update(focus);
				}
			}
			
		});
	}
	
}
