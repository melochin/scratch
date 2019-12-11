package scratch.dao.redis;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scratch.test.ContextTest;

import static org.junit.Assert.*;

public class CacheVoteDaoTest extends ContextTest {

	@Autowired
	private ILikeDao cacheVoteDao;

	@Test
	public void addSetTest() {
		cacheVoteDao.add(-1, new Long(-1));
		assertTrue(cacheVoteDao.getCount(new Long(-1)) == 1);
	}



}