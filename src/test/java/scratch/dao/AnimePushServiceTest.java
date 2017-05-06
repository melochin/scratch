package scratch.dao;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.service.bilibili.AnimePushService;
import scratch.test.ContextTest;

public class AnimePushServiceTest extends ContextTest{

	@Autowired
	private AnimePushService service;
	
	@Test
	public void test() throws InterruptedException {
		service.push();
		TimeUnit.SECONDS.sleep(10);
	}
	
}
