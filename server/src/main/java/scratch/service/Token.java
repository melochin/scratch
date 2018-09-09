package scratch.service;

public class Token {

	private String ip;

	private Long userId;

	public Token(String ip, Long userId) {
		this.ip = ip;
		this.userId = userId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
