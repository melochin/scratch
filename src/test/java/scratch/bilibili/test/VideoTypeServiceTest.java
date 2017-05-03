package scratch.bilibili.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.service.bilibili.VideoTypeService;
import scratch.test.ContextTest;

public class VideoTypeServiceTest extends ContextTest {

	@Autowired
	private VideoTypeService service;
	
	
/*	@Test
	public void listVideoCountTest() {
		System.out.println(service.listVideoCount());
	}*/
	
	@Test
	public void listTest() {
		System.out.println(service.list());
	}
	
	
}
