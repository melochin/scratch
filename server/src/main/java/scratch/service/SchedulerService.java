package scratch.service;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service
public class SchedulerService {

	private ThreadPoolTaskScheduler threadPoolTaskScheduler;

	@PostConstruct
	private void init() {
		threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.initialize();
		threadPoolTaskScheduler.setRemoveOnCancelPolicy(true);
	}

	@PreDestroy
	private void destroy() {
		threadPoolTaskScheduler.destroy();
	}


	public ThreadPoolTaskScheduler getThreadPoolTaskScheduler() {
		return this.threadPoolTaskScheduler;
	}

	public void setTask(Runnable task, Date startTime, long interval) {
		removeAllTask();
		if(interval > 0) {
			// 重复
			threadPoolTaskScheduler.scheduleWithFixedDelay(task, startTime, interval);
		} else {
			// 一次性
			threadPoolTaskScheduler.schedule(task, startTime);
		}
	}

	public void removeAllTask() {

		int beforeCount = threadPoolTaskScheduler.getScheduledThreadPoolExecutor().getQueue().size();

		threadPoolTaskScheduler.getScheduledThreadPoolExecutor()
				.getQueue()
				.removeIf((runnable -> true));

		int afterCount = threadPoolTaskScheduler.getScheduledThreadPoolExecutor().getQueue().size();
		System.out.println("定时任务池清空 size " + beforeCount + "->" + afterCount);
	}

}
