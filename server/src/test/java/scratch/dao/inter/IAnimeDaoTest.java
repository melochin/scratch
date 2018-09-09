package scratch.dao.inter;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import scratch.model.entity.Anime;
import scratch.test.ContextTest;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class IAnimeDaoTest extends ContextTest {

	@Autowired
	private IAnimeDao animeDao;

	@Test
	public void listWithAlias() throws Exception {
		animeDao.listWithAlias();
	}

	@Test
	public void save() throws Exception {
		for(int i=0; i<100; i++) {
			runSave(String.valueOf(i+4000));
		}
		TimeUnit.SECONDS.sleep(5);
	}

	@Rollback
	@Transactional
	public void runSave(String name) {
		new Thread(() -> {
			Anime anime = new Anime(name);
			animeDao.save(anime);
			System.out.println(anime.getId() + ":" + anime.getName());
		}).start();
	}

}