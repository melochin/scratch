package scratch.api.etest;

public class StudentInfo {

	private String username;

	private String password;

	private String kdid;

	private String neeaId;

	private boolean isLogin;

	public StudentInfo(String username, String password, String kdid, String neeaId) {
		this.username = username;
		this.password = password;
		this.kdid = kdid;
		this.neeaId = neeaId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getKdid() {
		return kdid;
	}

	public void setKdid(String kdid) {
		this.kdid = kdid;
	}

	public String getNeeaId() {
		return neeaId;
	}

	public void setNeeaId(String neeaId) {
		this.neeaId = neeaId;
	}

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean login) {
		isLogin = login;
	}
}
