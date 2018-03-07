package scratch.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import scratch.model.TaskTime;
import scratch.service.RedisService;
import scratch.service.SchedulerService;
import scratch.service.anime.AnimeScratchService;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ApiScratchInfoController {

	@Autowired
	private AnimeScratchService scratchService;

	@Autowired
	private SchedulerService schedulerService;

	@Autowired
	private RedisService redisService;

	@GetMapping(value = "/api/admin/scratch/runstatus", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map getRunstatus() {
		Map map = new HashMap();
		map.put("status", scratchService.isRun());
		return map;
	}

	@GetMapping(value = "/api/admin/scratch/runmessage", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map getRunMessage() {
		Map map = new HashMap();
		map.put("status", scratchService.isRun());
		map.put("messages", scratchService.getMessages());
		return map;
	}

	@GetMapping(value = "/api/admin/scratch/run", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map run() {
		Map map = new HashMap();
		scratchService.run();
		map.put("status", scratchService.isRun());
		return map;
	}

	@GetMapping(value = "/api/admin/scratch/run/time/{time}/interval/{interval}")
	public Map runInTime(@PathVariable("time") LocalTime time,
						  @PathVariable("interval") Integer interval) {

		LocalDateTime dateTime;
		if(shouldPlusOneDay(time)) {
			dateTime = LocalDateTime.of(LocalDate.now().plusDays(1), time);
		} else {
			dateTime = LocalDateTime.of(LocalDate.now(), time);
		}
		Date date = Jsr310Converters.LocalDateTimeToDateConverter.INSTANCE.convert(dateTime);

		redisService.set("runtime", new TaskTime(time, interval));
		interval = interval * 60 * 1000;
		System.out.println(date);
		System.out.println(interval);
		schedulerService.setTask(() -> {
			scratchService.run();
		}, date, interval);
		return runTime();
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

	private boolean shouldPlusOneDay(LocalTime time) {
		LocalTime localTimeNow = LocalTime.now();
		return (time.getHour() < localTimeNow.getHour()) ||
				(time.getHour() == localTimeNow.getHour() && time.getMinute() <= localTimeNow.getMinute());
	}
}
