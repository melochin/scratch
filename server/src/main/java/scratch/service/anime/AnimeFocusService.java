package scratch.service.anime;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import scratch.dao.inter.IAnimeDao;
import scratch.dao.inter.IAnimeFocusDao;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeFocus;
import scratch.model.entity.User;
import scratch.model.view.AnimeDisplay;

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
	public Map<Anime, Integer> findAllAnime(Long userId, String type, Integer focus) {
		Map<Anime, Integer> map = new LinkedHashMap<Anime, Integer>();
		List<AnimeFocus> focusList = focusDao.findByUserId(userId);
		List<Anime> animeList = null;
		animeList = animeDao.listIf(type, null);
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
	
	public List<AnimeDisplay> getAnimeFocus(List<Anime> animes, Long userId) {
		List<AnimeDisplay> animeDisplays = new ArrayList<AnimeDisplay>();
		List<AnimeFocus> focuss = focusDao.findByUserId(userId);
		for(Anime anime : animes) {
			AnimeDisplay animeDisplay = new AnimeDisplay(anime);
			boolean focus = false;
			for(AnimeFocus animeFocus : focuss) {
				if(animeFocus.getAnime().getId().equals(anime.getId())) {
					focus = true;
					break;
				}
			}
			animeDisplay.setFocus(focus);
			animeDisplays.add(animeDisplay);
		}
		return animeDisplays;
	}
	
	public void save(Long  animeId, Long userId) {
		User user = new User(userId);
		Anime anime = new Anime(animeId);
		AnimeFocus animeFocus = new AnimeFocus(anime, user);
		animeFocus.setLastPushTime(new Date());
		if(focusDao.findByAnimeAndUser(animeId, userId) != null) return;
		focusDao.save(animeFocus);
	}
	
	public void delete(Long animeId, Long userId) {
		focusDao.deleteByAnimeAndUser(animeId, userId);
	}
	
}
