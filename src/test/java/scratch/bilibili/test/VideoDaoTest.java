package scratch.bilibili.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.bilibili.dao.VideoDao;
import scratch.bilibili.model.Video;
import scratch.test.ContextClass;

public class VideoDaoTest extends ContextClass{

	@Autowired
	private VideoDao videoDao;
	
	@Test
	public void saveTest() {
		/*Video v = new Video(Long.valueOf(123));
		v.setPicUrl("www.baidu.coms");
		videoDao.saveOrUpdate(v, v.getAvid());*/
	}
	
	@Test
	public void listCountByTypeTest() {
		System.out.println(videoDao.listCountByType());
	}
	
}