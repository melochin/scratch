package scratch.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import scratch.dao.inter.IAnimeEpisodeDao;
import scratch.dao.inter.IAnimeEpisodeScratchDao;
import scratch.model.AnimeEpisode;
import scratch.model.AnimeEpisodeScratch;

@Service
public class AnimeEpisodeService {

	@Autowired
	private IAnimeEpisodeDao episodeDao;
	
	@Autowired
	private IAnimeEpisodeScratchDao episodeScratchDao;
	
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
	
	public List<AnimeEpisode> list() {
		return episodeDao.list();
	}
	
	public List<AnimeEpisode> listByAnimeId(Long animeId) {
		return episodeDao.listByAnimeId(animeId);
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
		if(episodeDao.delete(id) != 1) {
			throw new RuntimeException("删除失败");
		}
	}

	@Transactional
	public boolean modify(AnimeEpisode episode) {
		return episodeDao.modify(episode) == 1;
	}

	
}
