package scratch.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scratch.model.RedisKey;
import scratch.model.TaskTime;
import scratch.model.entity.ScratchDateRecord;
import scratch.model.view.RunInfo;
import scratch.service.RedisService;
import scratch.service.SchedulerService;
import scratch.service.anime.AnimeScratchService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@RestController
public class ApiScratchInfoController {

	@Autowired
	private AnimeScratchService scratchService;

	@Autowired
	private SchedulerService schedulerService;

	@Autowired
	private RedisService redisService;

	private static Runnable timeTask;

	private static ReentrantLock lock = new ReentrantLock();

	@PostConstruct
	public void init() {
		TaskTime taskTime = redisService.get("runtime");
		if (taskTime != null) {
			lock.lock();
			try {
				timeTask = () -> scratchService.run();
				Date date = convertToDate(taskTime.getStartTime());
				schedulerService.setTask(timeTask, date, taskTime.getInterval() * 60 * 1000);
			} finally {
				lock.unlock();
			}

		}
	}

	@PreDestroy
	public void destory() {
		schedulerService.removeAllTask();
	}


	@GetMapping(value = "/api/admin/scratch/run", produces = MediaType.APPLICATION_JSON_VALUE)
	public void run() {
		scratchService.run();
		return;
	}

	@GetMapping(value = "/api/admin/scratch/run/message", produces = MediaType.APPLICATION_JSON_VALUE)
	public RunInfo getRunMessage() {

		RunInfo runInfo = new RunInfo();
		runInfo.setRun(scratchService.isRun());
		runInfo.setTimeRun(timeTask != null);
		runInfo.setTaskTime(redisService.get(RedisKey.RUN_TIME));

		return runInfo;
		/*Map map = new HashMap();
		map.put("status", scratchService.isRun());
		map.put("messages", scratchService.getLogs());
		return map;*/
	}

	@GetMapping(value = "/api/admin/scratch/run/time/{time}/interval/{interval}")
	public void timeTask(@PathVariable("time") LocalTime time,
						 @PathVariable("interval") Integer interval) {

		Date date = convertToDate(time);
		redisService.set("runtime", new TaskTime(time, interval));
		interval = interval * 60 * 1000;

		lock.lock();
		try {
			timeTask = () -> scratchService.run();
			schedulerService.setTask(timeTask, date, interval);
		} finally {
			lock.unlock();
		}

		return;
	}

	@GetMapping(value = "/api/admin/scratch/run/time/shutdown")
	public void timeTaskShutDown() {
		redisService.delete(RedisKey.RUN_TIME);
		schedulerService.removeAllTask();
		timeTask = null;
	}


	@GetMapping(value = "/api/admin/scratch/run/time")
	public Map runTime() {
		Map map = new HashMap();
		TaskTime taskTime = redisService.get("runtime");
		map.put("nextTime", taskTime.getNextTime().toString());
		map.put("startTime", taskTime.getStartTime().toString());
		map.put("interval", taskTime.getInterval());
		return map;
	}


	//TODO RUN Record
	@GetMapping(value = "/api/admin/scratch/run/records/date")
	public List<ScratchDateRecord> dateRecordList() {
		return scratchService.listDateRecord();
	}

	//TODO 日志
	@GetMapping(value = "/api/admin/scratch/run/logs")
	public List<String> listLogs() {
		return scratchService.listLogs();
	}

	@GetMapping(value = "/api/admin/scratch/run/logs/file")
	public List<String> readLog(@RequestParam("name") String filename) throws IOException {
		return scratchService.readLog(filename);
	}


	private Date convertToDate(LocalTime localTime) {
		LocalDateTime dateTime;
		if (shouldPlusOneDay(localTime)) {
			dateTime = LocalDateTime.of(LocalDate.now().plusDays(1), localTime);
		} else {
			dateTime = LocalDateTime.of(LocalDate.now(), localTime);
		}
		return Jsr310Converters.LocalDateTimeToDateConverter.INSTANCE.convert(dateTime);
	}

	private boolean shouldPlusOneDay(LocalTime time) {
		LocalTime localTimeNow = LocalTime.now();
		return (time.getHour() < localTimeNow.getHour()) ||
				(time.getHour() == localTimeNow.getHour() && time.getMinute() <= localTimeNow.getMinute());
	}
}
