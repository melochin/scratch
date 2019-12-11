package scratch.config;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import scratch.service.scratch.NotifyTaskProducer;
import scratch.service.anime.AnimeScratchService;

import javax.annotation.PostConstruct;

@Configuration
public class TaskConfig {

	private final static Logger log = Logger.getLogger(TaskConfig.class);

	@Autowired
	private NotifyTaskProducer notifyTaskProducer;

	@Autowired
	private AnimeScratchService animeScratchService;

	static {
		log.debug("[Start] 定时任务");
	}

	// TODO 定时任务异常会被内部机制捕获
	@Scheduled(fixedRate = 1 * 60 * 1000)
	public void animeNotify() {
		log.debug("启动推送任务");
		notifyTaskProducer.push();
	}

	@PostConstruct
	public void animeScratchTask() {
		log.debug("设置定时抓取任务");
		animeScratchService.initTiming();
	}

}
