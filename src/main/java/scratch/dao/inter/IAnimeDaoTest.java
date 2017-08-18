package scratch.dao.inter;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.support.PageFactory;
import scratch.test.ContextTest;

public class IAnimeDaoTest extends ContextTest {

	@Autowired
	private IAnimeDao dao;
	
	//@Test
	public void queryTest() {
		dao.listIf(null, null);
		dao.listIf("1", null);
		dao.listIf("1", false);
		dao.listIf(null, true);
		dao.listIf(null, false);
	}
	
	@Test
	public void pageByTypeTest() {
		dao.pageByType(new String("11"), PageFactory.asList(1));
	}
	

}
