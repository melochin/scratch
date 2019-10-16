package scratch.model.view;

import scratch.model.TaskTime;

public class RunInfo {

	private boolean isRun;

	private TaskTime taskTime;

	public RunInfo(boolean isRun, TaskTime taskTime) {
		this.isRun = isRun;
		this.taskTime = taskTime;
	}

	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean run) {
		isRun = run;
	}

	public void setTaskTime(TaskTime taskTime) {
		this.taskTime = taskTime;
	}

	public boolean isTimeRun() {
		return taskTime != null;
	}

	public String getNextTime() {
		return taskTime != null ? taskTime.getNextTime().toString() : "";
	}

	public String getStartTime() {
		return taskTime != null ? taskTime.getStartTime().toString() : "";
	}

	public int getInterval() {
		return taskTime != null ? taskTime.getInterval() : 0;
	}
}
