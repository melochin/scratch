package scratch.service.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.service.ScratchService;
import scratch.test.ContextClass;

public class ScratchServiceTest extends ContextClass{

	@Autowired
	private ScratchService service;
	
	@Test
	public void alertSearchTest() {
		System.out.println(service.alertSearch(new Long(43)));
	} 
	
}
