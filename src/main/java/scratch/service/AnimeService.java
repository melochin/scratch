package scratch.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import scratch.dao.inter.IAnimeDao;
import scratch.model.Anime;

@Service
public class AnimeService {

	@Autowired
	private IAnimeDao animeDao;
	
	
	public List<Anime> findAll() {
		return animeDao.findAll();
	}
	
	
	public void save(Anime anime) {
		animeDao.save(anime);
	}
	
	public void update(Anime anime) {
		animeDao.update(anime);
	}
	
	public void delete(Long animeId) {
		animeDao.delete(animeId);
	}


	public Anime findById(Long animeId) {
		return animeDao.findById(animeId);
	}
	
}
