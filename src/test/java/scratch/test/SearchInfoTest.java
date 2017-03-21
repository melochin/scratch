package scratch.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import scratch.dao.SearchInfoDao;
import scratch.service.Page;

@Transactional
public class SearchInfoTest extends ContextClass{

	@Autowired
	SearchInfoDao dao;
	
	@Test
	public void listByTagTest() {
		Page p = new Page(1, 10);
		System.out.println(dao.listByTag(new Long(73) , new Long(12),p));
		System.out.println(p.getTotalPage());
	}
}
