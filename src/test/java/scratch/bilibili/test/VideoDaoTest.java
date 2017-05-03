package scratch.bilibili.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageRowBounds;

import scratch.dao.VideoDao;
import scratch.dao.inter.IVideoDao;
import scratch.model.Video;
import scratch.test.ContextTest;

public class VideoDaoTest extends ContextTest{

	@Autowired
	private VideoDao videoDao;
	
	@Autowired
	private IVideoDao dao;
	
	@Test
	@Rollback(true)
	@Transactional
	public void saveTest() {
		Video v = new Video(Long.valueOf(123));
		v.setPicUrl("www.baidu.coms");
		dao.save(v);
	}
	
	@Test
	public void listCountByTypeTest() {
		System.out.println(videoDao.listCountByType());
	}
	
	@Test
	public void queryTest() {
		List<Video> list = dao.list( Arrays.asList("11 22".split(" ")), 
				null, 
				VideoDao.ORDER_DATE,
				new PageRowBounds(1, 10));;
				
		System.out.println(dao.getById(new Long(2373)));
		System.out.println(dao.countsByType());
	}
	
}
