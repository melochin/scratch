package scratch.service.anime;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import scratch.dao.inter.IScratchRecord;
import scratch.model.entity.ScratchDateRecord;
import scratch.model.entity.ScratchRecord;
import scratch.service.FileService;

@Service
public class AnimeScratchService {

	@Autowired
	private IScratchRecord recordDao;
	
	@Autowired
	private FileService fileService;

	@Autowired
	private AnimeScratchTask scratchTask;


	public void run() {
		scratchTask.run();
	}

	/**
	 * 服务运行状态
	 * @return
	 */
	public boolean isRun() {
		return scratchTask.isRun();
	}


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


	// 运行记录
	public Map<String, Integer> getRecordMap() {
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		List<ScratchRecord> records = recordDao.list();
		for(ScratchRecord record : records) {
			long plus = (record.getEndTime().getTime() - record.getStartTime().getTime()) / 1000;
			plus = plus == 0 ? 1 : plus;
			long unit = record.getCount() / plus;
			String endTime = DateFormatUtils.format(record.getEndTime(), DateFormatUtils.ISO_TIME_NO_T_FORMAT.getPattern());
			map.put(endTime, new Integer((int) unit));
		}
		return map;
	}

	public List<ScratchDateRecord> listDateRecord() {
		return recordDao.groupByDate();
	}
}
