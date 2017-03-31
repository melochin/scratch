package scratch.bilibili.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.bilibili.dao.VideoTypeDao;
import scratch.test.ContextClass;

public class VideoTypeDaoTest extends ContextClass{

	@Autowired
	private VideoTypeDao typeDao;
	
	@Test
	public void queryTest() {
		System.out.println(typeDao.list(1));
	}
	
}
