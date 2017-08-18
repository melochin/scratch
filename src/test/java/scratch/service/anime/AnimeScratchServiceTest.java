package scratch.service.anime;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.test.ContextTest;

public class AnimeScratchServiceTest extends ContextTest {

	@Autowired
	AnimeScratchService service;

	@Test
	public void scratchTest() throws InterruptedException {
		service.run();
		TimeUnit.SECONDS.sleep(30);
		
	}
	
	//@Test
	public void queryTest() {
		System.out.println(service.getRecordMap());
	}
	
}
