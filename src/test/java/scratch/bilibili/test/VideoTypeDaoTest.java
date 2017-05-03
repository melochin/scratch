package scratch.bilibili.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.dao.inter.IVideoTypeDao;
import scratch.model.VideoType;
import scratch.test.ContextTest;

public class VideoTypeDaoTest extends ContextTest{

	@Autowired
	private IVideoTypeDao typeDao;
	
	@Test
	public void queryTest() {
		System.out.println(typeDao.list());
	}
	
	@Test
	public void saveAndUpdateTest() {
		VideoType type = new VideoType(new Integer(100));
		type.setName("test");
		type.setParentType(new VideoType(5));
		typeDao.save(type);
		type.setName("23333");
		typeDao.modify(type);
		typeDao.delete(100);
	}
	
}
