package scratch.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.collections.DefaultRedisMap;
import org.springframework.data.redis.support.collections.DefaultRedisSet;
import org.springframework.data.redis.support.collections.RedisMap;
import org.springframework.data.redis.support.collections.RedisSet;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import scratch.dao.inter.IAnimeDao;
import scratch.dao.inter.IAnimeEpisodeDao;
import scratch.dao.inter.IAnimeEpisodeScratchDao;
import scratch.dao.inter.IAnimeFocusDao;
import scratch.model.RedisKey;
import scratch.model.entity.AnimeEpisode;
import scratch.model.entity.AnimeEpisodeScratch;
import scratch.model.ohter.UserAdapter;
import scratch.model.view.AnimeEpisodeDisplay;
import scratch.support.service.PageBean;

@Service
public class AnimeEpisodeService {

	@Autowired
	private IAnimeDao animeDao;

	@Autowired
	private IAnimeEpisodeDao episodeDao;
	
	@Autowired
	private IAnimeEpisodeScratchDao episodeScratchDao;

	@Autowired
	private IAnimeFocusDao focusDao;
	
	@Autowired
	private ConversionService conversionService;

	@Autowired
	private RedisTemplate redisTemplate;

	public AnimeEpisode getById(Long id) {
		return episodeDao.getById(id);
	}
	
	public List<AnimeEpisode> listTodayPass() {
		LocalDateTime nowTime = LocalDateTime.now(); 
		ZoneId zone = ZoneId.systemDefault();
		Date beginTime = Date.from(nowTime.withHour(0).withMinute(0).withSecond(0).atZone(zone).toInstant());
		Date endTime = Date.from(nowTime.withHour(23).withMinute(59).withSecond(59).atZone(zone).toInstant());
		return episodeDao.findByTime(beginTime, endTime);
	}

	@Transactional
	public List<AnimeEpisode> listOrderByTime(int limit) {
		return episodeDao.listOrderByTime(limit, null);
	}

	@Transactional
	public List<AnimeEpisode> listOrderByTime(int limit, String animeType) {
		return episodeDao.listOrderByTime(limit, animeType);
	}

	public List<AnimeEpisode> listOrderByUser(long userId) {

		List<AnimeEpisode> episodes = new ArrayList<>();

		focusDao.listByUserId(userId).stream()
				.forEach(focus -> episodes.addAll(episodeDao.listByAnimeId(focus.getAnime().getId())));

		episodes.forEach(e -> {
			e.setAnime(animeDao.getById(e.getAnime().getId()));
		});

		if(episodes.size() > 0) {
			episodes.sort((e1, e2) -> e1.getScratchTime().after(e2.getScratchTime())  ? -1 : 1);
		}
		return episodes;
	}

	/**
	 * 额外数据： 1.hot 2.anime所有属性
	 *
	 * @param animeEpisode
	 * @param userAdapter
	 * @return
	 */
	public AnimeEpisodeDisplay convertToDisplay(AnimeEpisode animeEpisode, UserAdapter userAdapter) {
		AnimeEpisodeDisplay display = new AnimeEpisodeDisplay(animeEpisode);
		display.setHot(getHot(animeEpisode.getId()));
		display.setAnime(animeDao.getById(animeEpisode.getAnime().getId()));
		return display;
	}


	public PageBean<AnimeEpisode> pageByFocusUser(Long userId, Integer page) {
		PageHelper.startPage(page, 5);
		return new PageBean<AnimeEpisode>(episodeDao.pageByFocusUser(userId));
	}

	public PageBean<AnimeEpisode> pageByFocusUser(Long userId, Integer pageStart, Integer pageEnd) {

		pageEnd = pageEnd < pageStart ? pageStart : pageEnd;

		PageBean<AnimeEpisode> first = pageByFocusUser(userId, pageStart);
		pageEnd = pageEnd > first.getPage().getTotal() ? first.getPage().getTotal() : pageEnd;

		for(int i=2; i<=pageEnd; i++) {

			PageBean<AnimeEpisode> temp = pageByFocusUser(userId, i);
			first.setPage(temp.getPage());
			first.getData().addAll(temp.getData());
		}

		return first;
	}




	public List<AnimeEpisode> list() {
		return episodeDao.list();
	}
	
	public List<AnimeEpisode> listByAnimeId(Long animeId) {
		return episodeDao.listByAnimeId(animeId);
	}

	public AnimeEpisode listByUrl(String url) {
		return episodeDao.findByUrl(url);
	}

	public AnimeEpisode getByAnimeIdAndHostIdAndNo(Long animeId, Long hostId, String no) {
		return episodeDao.getByAnimeIdAndHostIdAndNo(animeId, hostId, no);
	}

	public List<AnimeEpisodeScratch> listScratch(Integer status) {
		return episodeScratchDao.listByStatus(status);
	}

	public AnimeEpisodeScratch getScratch(Long id) {
		return episodeScratchDao.getById(id);
	}
	
	/**
	 * saveTime 自动设置为当前时间
	 * @param episode
	 */
	@Transactional
	public void save(AnimeEpisode episode) {
		episode.setSaveTime(new Date());
		episodeDao.save(episode);
	}
	
	@Transactional
	public void save(AnimeEpisodeScratch episodeScratch) {
		episodeScratchDao.save(episodeScratch);
	}


	/*-------------------------------------临时数据审核-----------------------------------------------------*/

	public Map<String, Long> listCountByStatus() {
		Map<String, Long> result = new HashMap<>();
		List<Map> listMap = episodeScratchDao.listCountByStatus();

		listMap.forEach(m -> {
			Integer status = (Integer) m.get("status");
			Long size = (Long) m.get("size");
			if(status.equals(0)) {
				result.put("waitLength", size);
			}

			if(status.equals(-1)) {
				result.put("rejectLength", size);
			}

		});
		return result;
	}


	//TODO Scratch删除，新增到episode

	/**
	 * 删除scratch,新增到episode
	 * @param episodeScratch
	 */
	@Transactional
	public void passScratch(AnimeEpisodeScratch episodeScratch) {
		Long scratchId = episodeScratch.getId();
		AnimeEpisode episode = new AnimeEpisode(episodeScratch);
		episodeScratchDao.delete(scratchId);
		save(episode);
	}

	/**
	 * 如果根据 animeId hostId no 在 episodes中能找到相同数据
	 * 则修改数据
	 * 否则新增数据
	 *
	 * scratch 数据通过之后，一律删除
	 *
	 * @param scratchId
	 */
	@Transactional
	public void passScratch(Long scratchId) {

		AnimeEpisodeScratch scratch = episodeScratchDao.getById(scratchId);
		AnimeEpisode episode = episodeDao.getByAnimeIdAndHostIdAndNo(
				scratch.getAnime().getId(),
				scratch.getHostId(),
				scratch.getNumber());
		if(episode != null) {
			// 正式数据中存在，执行更新
			episode.setUrl(scratch.getUrl());
			episode.setSaveTime(new Date());
			episodeDao.modify(episode);

		} else {
			// 正式数据中不存在，执行新增
			episode = new AnimeEpisode((scratch));
			episode.setSaveTime(new Date());
			episodeDao.save(episode);
		}
		// 最终删除临时数据
		episodeScratchDao.delete(scratchId);
	}


	@Transactional
	public void cover(Long scratchId, Long episodeId) {
		AnimeEpisode animeEpisode = episodeDao.getById(episodeId);
		AnimeEpisodeScratch animeEpisodeScratch = episodeScratchDao.getById(scratchId);
		animeEpisode.setUrl(animeEpisodeScratch.getUrl());
		modifyScratchStatus(animeEpisodeScratch.getId(), new Integer(1));
		episodeDao.modify(animeEpisode);
	}

	/**
	 * 拒绝的留着： 形成一个抓取库，避免保存重复数据
	 * @param scratchId
	 */
	@Transactional
	public void rejectScratch(Long scratchId) {
		AnimeEpisodeScratch episodeScratch = episodeScratchDao.getById(scratchId);
		modifyScratchStatus(episodeScratch.getId(), new Integer(-1));
	}
	
	
	@Transactional
	public void modifyScratchStatus(Long id, Integer status) {
		episodeScratchDao.modifyStatus(id, status);
	}
	
	/**
	 * 
	 * @param status
	 * 0 待审核
	 * 1 通过审核
	 * -1 审核失败
	 * @return
	 */
	public Integer countByScratchStatus(Integer status) {
		return episodeScratchDao.countByStatus(status);
	}
	
	@Transactional
	public void delete(Long id) {
		episodeDao.delete(id);
	}

	@Transactional
	public boolean modify(AnimeEpisode episode) {
		return episodeDao.modify(episode) == 1;
	}


	public Date getLastUpdatedTime(Long animeId) {
		return episodeDao.getLastUpdatedTime(animeId);
	}

	public void countHot(Long episodeId, String ip) {
		RedisMap<Long, Long> hotMap = new DefaultRedisMap<Long, Long>(RedisKey.episodeHot(), redisTemplate);
		RedisMap<String, Set<Long>> ipMap = new DefaultRedisMap<String, Set<Long>>(RedisKey.episodeIp(), redisTemplate);

		if(ipMap.isEmpty()) {
			ipMap.expire(1, TimeUnit.DAYS);
		}

		// 判断 ip 是否点击过该episode
		Set<Long> episdoeIds = ipMap.get(ip);
		if(episdoeIds != null && episdoeIds.contains(episodeId)) return;

		// 若没有，增加episode热度
		//TODO 考虑原子性 （increment 函数不能使用， 保存时数字没有序列化，导致反序列失败）
		Long count = hotMap.get(episodeId);
		if(count == null) {
			count = new Long(0);
		}
		count++;
		hotMap.put(episodeId, count);

		// 保存 ip 点击 episode记录
		// 有效期 1天
		if(episdoeIds == null) {
			episdoeIds = new HashSet<>();
		}
		episdoeIds.add(episodeId);
		ipMap.put(ip, episdoeIds);

		return;
	}

	public Long getHot(Long episodeId) {
		RedisMap<Long, Long> hostMap = new DefaultRedisMap<Long, Long>(RedisKey.episodeHot(), redisTemplate);
		Long hot = hostMap.get(episodeId);
		if(hot == null) {
			hot = new Long(0);
		}
		return hot;
	}
}
