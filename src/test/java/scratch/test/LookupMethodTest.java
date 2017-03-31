package scratch.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.service.PageManager;
import scratch.service.SearchInfoService;

public class LookupMethodTest extends ContextClass{

	@Autowired
	private SearchInfoService service;
	
	@Autowired
	private PageManager p;
	
	
	@Test
	public void test() {
		System.out.println(service.getPage());
		System.out.println(service.getPage());
	
		System.out.println(p.createPage());
		System.out.println(p.createPage());
	}
	
}
