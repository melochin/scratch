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
	private WebFileService fileService;

	@Autowired
	private RedisService redisService;

	@Autowired
	private SchedulerService schedulerService;

	@Autowired
	private IScratchTaskProducer scratchTaskProducer;

	@Autowired
	private RunInfoWebSocket runInfoWebSocket;

	private volatile boolean isRun = false;

	@PostConstruct
	public void initTiming() {
		TaskTime taskTime = redisService.get("runtime", TaskTime.class);
		if (taskTime == null) return;
		// 启动定时任务
		runTiming(taskTime);
	}

	@PreDestroy
	public void destory() {
		schedulerService.removeAllTask();
	}

	public void runTiming(TaskTime taskTime) {
		// 清除所有定时任务
		schedulerService.removeAllTask();
		// 重新设置定时时间
		redisService.set("runtime", taskTime);
		// 时间转换
		Date date = convertToDate(taskTime.getNextTime());
		long interval = taskTime.getInterval() * 60 * 1000;
		doRunTiming(date, interval);
	}

	private void doRunTiming(Date date, long interval) {
		System.out.println(date);
		System.out.println(interval);
		schedulerService.setTask(
				() -> scratchTaskProducer.produce(),
				date,
				interval);
	}

	/**
	 * 运行服务
	 */
	public void run() {

		if (isRun) return;
		isRun = true;
		new Thread(() -> {
			scratchTaskProducer.produceAndWait();
			isRun = false;
			runInfoWebSocket.sendMessage(getRunMessage());
		}).start();

	}

	public RunInfo getRunMessage() {
		return new RunInfo(isRun, redisService.get(RedisKey.RUN_TIME));
	}

	public void shutdownTiming() {
		redisService.delete(RedisKey.RUN_TIME);
		schedulerService.removeAllTask();
	}

	// 日志相关



	/**
	 * 读取 "/logs" 文件夹下 "scratch"开头的日志文件名
	 * 排序：文件最后次修改时间 逆序
	 * @return
	 */
	public List<String> listLogs() {
		// 获取文件
		File file = fileService.getFile("/logs");
		if(file == null || !file.exists()) return null;

		File[] logArray = file.listFiles((dirc, name) -> name.startsWith("scratch"));
		if(logArray == null || logArray.length == 0) return null;
		// 排序
		List<File> logList = Arrays.asList(logArray);
		logList.sort((f1, f2) -> f1.lastModified() > f2.lastModified() ? -1 : 1);
		// 输出文件名
		return logList.stream()
				.map(f -> f.getName())
				.collect(Collectors.toList());
	}

	public List<String> readLog(String filename) throws IOException {
		// 读取日志文件
		List<String> logRecords = new ArrayList<>();

		File file = fileService.getFile("/logs/" + filename);
		if(file == null || !file.exists()) return null;

		// 输出文件的每行记录
		BufferedReader read = new BufferedReader(new FileReader(file));
		String record = null;
		while((record = read.readLine()) != null) {
			logRecords.add(record);
		}
		read.close();

		return logRecords;
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

	public List<ScratchLog> listLogsByDate(Date date) {
		return scratchLogDao.listByDate(date);
	}
}
