package scratch.bilibili.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.dao.VideoScratchRecordDao;
import scratch.test.ContextTest;

public class VideoScratchRecordDaoTest extends ContextTest {

	@Autowired
	private VideoScratchRecordDao dao;
	
	@Test
	public void  getRecentTest(){
		System.out.println(dao.getRecent().getId());
	}
}
