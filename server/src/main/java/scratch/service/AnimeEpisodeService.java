package scratch.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import scratch.dao.inter.IAnimeDao;
import scratch.dao.inter.IAnimeEpisodeDao;
import scratch.dao.inter.IAnimeEpisodeScratchDao;
import scratch.dao.inter.IAnimeFocusDao;
import scratch.model.entity.AnimeEpisode;
import scratch.model.entity.AnimeEpisodeScratch;

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
	
	@Transactional
	public void passScratch(Long scratchId) {
		AnimeEpisodeScratch episodeScratch = episodeScratchDao.getById(scratchId);
		AnimeEpisode episode = conversionService.convert(episodeScratch, AnimeEpisode.class);
		modifyScratchStatus(episodeScratch.getId(), new Integer(1));
		save(episode);
	}
	
	@Transactional
	public void passScratch(AnimeEpisodeScratch episodeScratch) {
		AnimeEpisode episode = conversionService.convert(episodeScratch, AnimeEpisode.class);
		modifyScratchStatus(episodeScratch.getId(), new Integer(1));
		save(episode);
	}

	@Transactional
	public void cover(Long scratchId, Long episodeId) {
		AnimeEpisode animeEpisode = episodeDao.getById(episodeId);
		AnimeEpisodeScratch animeEpisodeScratch = episodeScratchDao.getById(scratchId);
		animeEpisode.setUrl(animeEpisodeScratch.getUrl());
		modifyScratchStatus(animeEpisodeScratch.getId(), new Integer(1));
		episodeDao.modify(animeEpisode);
	}

	@Transactional
	public void rejectScratch(Long scratchId) {
		AnimeEpisodeScratch episodeScratch = episodeScratchDao.getById(scratchId);
		if(new Integer(1).equals(episodeScratch.getStatus())) {
			// 先删除animeEpisode的数据
			if(episodeDao.deleteByUrl(episodeScratch.getUrl()) > 1) {
				throw new RuntimeException();
			}
		}
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
}
