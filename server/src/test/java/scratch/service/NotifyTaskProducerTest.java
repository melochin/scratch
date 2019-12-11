package scratch.service;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.service.scratch.NotifyTaskProducer;
import scratch.test.ContextTest;

public class NotifyTaskProducerTest extends ContextTest{

	@Autowired
	private NotifyTaskProducer service;
	
	@Test
	public void test() throws InterruptedException {
		service.push();
		TimeUnit.SECONDS.sleep(10);
	}
	
}
