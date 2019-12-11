package scratch.dao.inter;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import scratch.model.entity.AnimeAlias;
import scratch.test.ContextTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class IAnimeAliasDaoTest extends ContextTest {

	@Autowired
	private IAnimeAliasDao animeAliasDao;

	@Test
	public void list() throws Exception {
		List<AnimeAlias> animeAliasList = animeAliasDao.list(new Long(179));
		assertTrue(animeAliasList.get(0) != null);
		System.out.println(animeAliasList);
	}

	@Test
	public void find() {
		AnimeAlias animeAlias = animeAliasDao.find(new Long(179), new Long(3));
		System.out.println(animeAlias);
	}

	@Test
	@Rollback
	@Transactional
	public void save() {
		List<AnimeAlias> animeAliasList = new ArrayList<>();
/*		animeAliasList.add(new AnimeAlias(new Long(3), "1", "2", "3"));
		animeAliasList.add(new AnimeAlias(new Long(2), "4", "3", "6"));
		animeAliasDao.save(new Long(100), animeAliasList);*/
	}

	@Test
	public void delete() {
		animeAliasDao.delete(new Long(100));
	}

}