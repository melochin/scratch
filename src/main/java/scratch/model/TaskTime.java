package scratch.model;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class TaskTime implements Serializable {

	private LocalTime startTime;

	/**
	 * 单元：分
	 */
	private int interval;

	public TaskTime(LocalTime startTime, int interval) {
		this.startTime = startTime;
		this.interval = interval;
	}

	public LocalTime getNextTime() {
		LocalTime temp = startTime;
		while(LocalTime.now().compareTo(temp) > 0) {
			int before =temp.getHour();
			temp = temp.plus(interval, ChronoUnit.MINUTES);
			if(temp.getHour() < before) {
				break;
			}
		}
		return temp;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public int getInterval() {
		return interval;
	}
}
