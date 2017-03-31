package scratch.bilibili.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.bilibili.dao.VideoScratchRecordDao;
import scratch.test.ContextClass;

public class VideoScratchRecordDaoTest extends ContextClass {

	@Autowired
	private VideoScratchRecordDao dao;
	
	@Test
	public void  getRecentTest(){
		System.out.println(dao.getRecent().getId());
	}
}
