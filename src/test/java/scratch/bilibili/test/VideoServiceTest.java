package scratch.bilibili.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import scratch.bilibili.dao.VideoDao;
import scratch.bilibili.service.VideoService;
import scratch.test.ContextClass;

public class VideoServiceTest extends ContextClass{

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

