package scratch.model.entity;

import java.io.Serializable;
import java.util.Date;

public class MemoryCardInfo implements Serializable {

	// 记住次数
	private Integer remeber;

	// 忘记次数
	private Integer forget;

	// 上次记忆时间
	private Date lastTime;

	public MemoryCardInfo() {
		this.lastTime = new Date();
		this.remeber = 0;
		this.forget = 0;
	}

	public Integer getRemeber() {
		return remeber;
	}

	public void setRemeber(Integer remeber) {
		this.remeber = remeber;
	}

	public Integer getForget() {
		return forget;
	}

	public void setForget(Integer forget) {
		this.forget = forget;
	}

	public Date getLastTime() {
		return lastTime;
	}

	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}

	public void increRemeber() {
		this.lastTime = new Date();
		this.remeber++;
	}

	public void increForget() {
		this.lastTime = new Date();
		this.forget++;
	}

	@Override
	public String toString() {
		return "MemoryCardInfo{" +
				"remeber=" + remeber +
				", forget=" + forget +
				", lastTime=" + lastTime +
				'}';
	}
}
