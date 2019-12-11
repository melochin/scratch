package scratch.service.anime;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.stereotype.Service;

import scratch.dao.inter.IScratchLogDao;
import scratch.model.RedisKey;
import scratch.model.TaskTime;
import scratch.model.entity.ScratchLog;
import scratch.model.view.RunInfo;
import scratch.service.RedisService;
import scratch.service.SchedulerService;
import scratch.service.WebFileService;
import scratch.service.scratch.IScratchTaskProducer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class AnimeScratchService {

	@Autowired
	private IScratchLogDao scratchLogDao;

	@Autowired
	private RedisService redisService;

	@Autowired
	private SchedulerService schedulerService;

	@Autowired
	private IScratchTaskProducer scratchTaskProducer;

	@Autowired
	private RunInfoWebSocket runInfoWebSocket;

	private volatile boolean isRun = false;

	/**
	 * 系统启动时，初始化定时任务
	 */
	public void initTiming() {
		TaskTime taskTime = redisService.get(RedisKey.RUN_TIME, TaskTime.class);
		if (taskTime == null) return;

		runTiming(taskTime);
	}

	/**
	 * 运行定时任务
	 * @param taskTime (minute)
	 */
	public void runTiming(TaskTime taskTime) {
		// 清除所有定时任务
		schedulerService.removeAllTask();
		// 重新设置定时时间
		redisService.set(RedisKey.RUN_TIME, taskTime);

		// 时间转换
		Date date = convertToDate(taskTime.getNextTime());
		long interval = taskTime.getInterval() * 60 * 1000;

		doRunTiming(date, interval);
	}

	/**
	 * 启动定时任务，定时向消息队列生产消息
	 * @param date
	 * @param interval
	 */
	private void doRunTiming(Date date, long interval) {
		schedulerService.setTask(
				() -> scratchTaskProducer.produce(),
				date,
				interval);
	}

	/**
	 * 关闭定时任务
	 */
	public void shutdownTiming() {
		redisService.delete(RedisKey.RUN_TIME);
		schedulerService.removeAllTask();
	}

	@PreDestroy
	public void destory() {
		schedulerService.removeAllTask();
	}

	/**
	 * 运行一次性服务
	 */
	public void run() {

		if (isRun) return;
		isRun = true;

		new Thread(() -> {
			// 同步等待消息队列信息
			scratchTaskProducer.produceAndWait();
			isRun = false;
			// socket实时推送
			runInfoWebSocket.sendMessage(getRunMessage());
		}).start();

	}

	public RunInfo getRunMessage() {
		return new RunInfo(isRun, redisService.get(RedisKey.RUN_TIME));
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

	/**
	 * 统计days的成功和失败次数
	 * @param days
	 * @return
	 */
	public List listLogsForFigure(int days) {
		return IntStream.rangeClosed(0, days)
				// 生成从现在开始到前days的日期
				.mapToObj(day -> new Date(System.currentTimeMillis() - day * 24 * 60 * 60 * 1000))
				.sorted()
				// 使用日期去检索当天的成功数和失败数
				.map(date -> convertToArray(date, scratchLogDao.groupByType(date)))
				.collect(Collectors.toList());
	}

	private Object[] convertToArray(Date date, List<Map<String, Object>> listMap) {
		Object[] array = new Object[3];
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		// set default value
		array[0] = formatter.format(date);
		array[1] = 0;
		array[2] = 0;

		for(Map<String, Object> map : listMap) {
			if ((Integer)map.get("type") == 0) {
				array[1] = map.get("count");
			} else {
				array[2] = map.get("count");
			}
		}
		return array;
	}

	/**
	 * 列出date那天的日志记录
	 * @param date
	 * @return
	 */
	public List<ScratchLog> listLogsByDate(Date date) {
		return scratchLogDao.listByDate(date);
	}
}
