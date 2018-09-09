package scratch.dao.inter;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scratch.model.entity.AnimeEpisode;
import scratch.test.ContextTest;

import static org.junit.Assert.*;

public class IAnimeEpisodeDaoTest extends ContextTest {

	@Autowired
	private IAnimeEpisodeDao dao;

	@Test
	public void getLastUpdatedTime() throws Exception {
		System.out.println(dao.getLastUpdatedTime(new Long(175)));
	}

	@Test
	public void getByAnimeIdAndHostIdAndNo() throws Exception {
		dao.getByAnimeIdAndHostIdAndNo(new Long(1), new Long(1), "1");
	}

	@Test
	public void listByFocusUser() {
		System.out.println(dao.listByFocusUser(35));
	}

}