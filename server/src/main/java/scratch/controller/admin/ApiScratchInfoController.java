package scratch.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import scratch.model.TaskTime;
import scratch.model.entity.ScratchLog;
import scratch.model.view.RunInfo;
import scratch.service.anime.AnimeScratchService;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@RestController
public class ApiScratchInfoController {

	@Autowired
	private AnimeScratchService scratchService;

	/**
	 * 开启即时任务
	 * @return
	 */
	@CrossOrigin
	@PostMapping(value = "/api/admin/scratch/run", produces = MediaType.APPLICATION_JSON_VALUE)
	public RunInfo run() {
		scratchService.run();
		return scratchService.getRunMessage();
	}

	/**
	 * 获取运行信息（包括即时和定时）
	 * @return
	 */
	@CrossOrigin
	@GetMapping(value = "/api/admin/scratch/run/message", produces = MediaType.APPLICATION_JSON_VALUE)
	public RunInfo getRunMessage() {
		return scratchService.getRunMessage();
	}

	/**
	 * 开启定时任务
	 * @param time
	 * @param interval
	 */
	@CrossOrigin
	@PostMapping(value = "/api/admin/scratch/run/time/{time}/interval/{interval}")
	public RunInfo timeTask(@PathVariable("time") LocalTime time,
						 @PathVariable("interval") Integer interval) {
		scratchService.runTiming(new TaskTime(time, interval));
		return scratchService.getRunMessage();
	}

	/**
	 * 关闭定时任务
	 */
	@CrossOrigin
	@DeleteMapping(value = "/api/admin/scratch/run/time")
	public RunInfo shutdownTiming() {
		scratchService.shutdownTiming();
		return scratchService.getRunMessage();
	}

	/**
	 * 列出近days天的任务成功失败数量统计
	 * @param days
	 * @return
	 */
	@CrossOrigin
	@GetMapping(value = "/api/admin/scratch/run/logs/figure/{days}")
	public List listLogsForFigure(@PathVariable("days") int days) {
		days = days >= 31 ? 31 : days;
		return scratchService.listLogsForFigure(days);
	}

	/**
	 * 读取指定一天的详细日志记录
	 * @param date
	 * @return
	 */
	@CrossOrigin
	@GetMapping(value = "/api/admin/scratch/run/logs/{date}")
	public List<ScratchLog> listgLogsByDate(@DateTimeFormat(pattern = "yyyy-MM-dd")
											   @PathVariable("date") Date date) {
		return scratchService.listLogsByDate(date);
	}

}
