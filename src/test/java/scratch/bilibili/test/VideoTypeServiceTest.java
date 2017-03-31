package scratch.bilibili.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.bilibili.service.VideoTypeService;
import scratch.test.ContextClass;

public class VideoTypeServiceTest extends ContextClass {

	@Autowired
	private VideoTypeService service;
	
	
	@Test
	public void listVideoCountTest() {
		System.out.println(service.listVideoCount());
	}
	
	
	
}
