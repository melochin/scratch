package scratch.bilibili.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import scratch.service.bilibili.VideoService;
import scratch.dao.VideoDao;
import scratch.test.ContextTest;

public class VideoServiceTest extends ContextTest{

	@Autowired
	private VideoService service;
	
	@Test
	@Transactional
	public void listVideoTest() {
		service.list(new Long(73), new Integer(1));
	}
	
	@Test
	@Transactional
	public void listVideoHotTest() {
		System.out.println(service.list(null, VideoDao.ORDER_PLAY, new Integer(1)));
	}
	
	
	
}

