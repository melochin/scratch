package scratch.service.test;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import scratch.bilibili.service.ScratchService;
import scratch.test.ContextClass;

public class ScratchServiceTest extends ContextClass{

	@Autowired
	private ScratchService service;
	
	@Test
	@Rollback(false)
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void scratchTest() throws InterruptedException {
		System.out.println(service.isRun());
		service.scratchVideo();
		TimeUnit.SECONDS.sleep(180);
		System.out.println(service.isRun());
	}
	
//	@Test
	@Rollback(false)
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void isRunTest() throws InterruptedException {
		int i = 0;
		while(i<100) {
			TimeUnit.SECONDS.sleep(1);
			System.out.println(service.isRun());
			i++;
		}
	}
	
//	@Test
	public void SchedulerTest() throws IllegalStateException, InterruptedException, ExecutionException {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 10);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        System.out.println(c.getTime());
	}
	
	
}
