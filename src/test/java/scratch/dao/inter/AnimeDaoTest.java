package scratch.dao.inter;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import scratch.model.Anime;
import scratch.test.ContextTest;

public class AnimeDaoTest extends ContextTest {

	@Autowired
	private IAnimeDao dao;
	
	@Test
	public void queryTest() {
		System.out.println(dao.listByTypeLeftJointFocus("11"));
	}
	
	@Test
	public void testFindAll() {
		System.out.println(dao.list());
	}

	@Test
	public void testFindByAlias() {
		dao.findByAlias("进击的巨人第二季", false);
	}

	@Test
	@Rollback(true)
	@Transactional
	public void testSave() {
		Anime anime = new Anime("TEST ANIME");
		dao.save(anime);
	}
	
	@Rollback(true)
	@Transactional
	public void testUpdate() {
		Anime anime = new Anime(new Long(1));
		anime.setName("TEST ANIME");
		dao.update(anime);
	}

	@Test
	@Rollback(true)
	@Transactional
	public void testDelete() {
		dao.delete(new Long(1));
	}

}
