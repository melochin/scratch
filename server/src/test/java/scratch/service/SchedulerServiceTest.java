package scratch.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class SchedulerServiceTest {

	private SchedulerService schedulerService = new SchedulerService();

	@Test
	public void taskTest() throws InterruptedException {
		schedulerService.setTask(() -> {
			System.out.println("runnable 1");
		}, new Date(), 1000);

		assertTrue(schedulerService.getThreadPoolTaskScheduler()
				.getScheduledThreadPoolExecutor().getQueue().size() == 1);
		TimeUnit.SECONDS.sleep(1);
		assertTrue(schedulerService.getThreadPoolTaskScheduler()
				.getScheduledThreadPoolExecutor().getQueue().size() == 1);


		TimeUnit.SECONDS.sleep(1);
		schedulerService.setTask(() -> {
			System.out.println("runnable 2");
		}, new Date(), 1000);
		TimeUnit.MILLISECONDS.sleep(500);
		assertTrue(schedulerService.getThreadPoolTaskScheduler()
				.getScheduledThreadPoolExecutor().getQueue().size() == 1);
	}

}