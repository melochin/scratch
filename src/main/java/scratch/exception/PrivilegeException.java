package scratch.exception;

@SuppressWarnings("serial")
public class PrivilegeException extends RuntimeException {

	public PrivilegeException() {
		super();
	}
	
	public PrivilegeException(String message) {
		super(message);
	}

}
