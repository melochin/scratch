package scratch.exception;

@SuppressWarnings("serial")
public class PrivilegeException extends RuntimeException {

	public final static String NOLOGIN = "请先登录";
	
	public final static String NOACTIVI = "账号尚未激活";
	
	
	public PrivilegeException() {
		super();
	}
	
	public PrivilegeException(String message) {
		super(message);
	}

}
