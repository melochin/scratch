package scratch.service.anime;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import scratch.dao.inter.IAnimeDao;
import scratch.dao.inter.IAnimeFocusDao;
import scratch.model.Anime;
import scratch.model.AnimeFocus;
import scratch.model.User;

@Service
public class AnimeFocusService {

	@Autowired
	private IAnimeFocusDao focusDao;
	
	@Autowired
	private IAnimeDao animeDao;
	
	/**
	 * 查询所有的Anime，但是有标志判断该Anime是否处于关注状态
	 * @param userId
	 * @return
	 */
	public Map<Anime, Integer> findAllAnime(Long userId, Integer type, Integer focus) {
		Map<Anime, Integer> map = new LinkedHashMap<Anime, Integer>();
		List<AnimeFocus> focusList = focusDao.findByUserId(userId);
		List<Anime> animeList = null;
	
		if(type == null) {
			animeList = animeDao.findAll();
		} else {
			animeList = animeDao.findByType(type);
		}
		
		for(Anime anime : animeList) {
			Integer focused = 0;
			for(AnimeFocus animeFocus : focusList) {
				if(animeFocus.getAnime().getId().equals(anime.getId())) {
					focused = 1;
					break;
				}
			}
			if(focus == null) {
				map.put(anime, focused);
			} else if(focus == focused) {
				map.put(anime, focused);
			}
		}
		return map;
	}
	
	public void save(Anime anime, User user) {
		AnimeFocus animeFocus = new AnimeFocus(anime, user);
		animeFocus.setLastPushTime(new Date());
		focusDao.save(animeFocus);
	}
	
	public void delete(Anime anime, User user) {
		focusDao.deleteByAnimeAndUser(anime.getId(), user.getUserId());
	}
	
}
