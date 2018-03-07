package scratch.service;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;
import scratch.service.anime.AnimeScratchService;
import scratch.test.ContextTest;

@Transactional
public class AnimeScratchServiceTest extends ContextTest {

	@Autowired
	AnimeScratchService service;

	@Test
	public void scratchTest() throws InterruptedException {
		service.run();
		TimeUnit.SECONDS.sleep(60);
		
	}
	
	//@Test
	public void queryTest() {
		System.out.println(service.getRecordMap());
	}
	
}
