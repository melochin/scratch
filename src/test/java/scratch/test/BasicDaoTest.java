package scratch.test;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import scratch.dao.SearchTypeDao;
import scratch.model.dictionary.SearchType;

public class BasicDaoTest extends ContextClass{

	@Autowired
	private SearchTypeDao dao;
	
	@Transactional
	@Test
	public void saveTest() {
		SearchType t = new SearchType();
		t.setCode(new Long(150));
		t.setName("XX");
		//数据保存测试
		dao.save(t);
		//获取数据
		SearchType t2= dao.get(SearchType.class, new Long(150));
		//结论验证
		Assert.assertEquals(t, t2);
	}
	
	@Transactional
	@Test
	public void updateTest() {
		SearchType t = new SearchType();
		t.setCode(new Long(0));
		t.setName("XX");
		dao.update(t);
		SearchType t2 = dao.get(SearchType.class, new Long(0));
		Assert.assertEquals(t, t2);
		dao.update(t, new Long(0));
		t2 = dao.get(SearchType.class, new Long(0));
		Assert.assertEquals(t, t2);
	}
	
}
