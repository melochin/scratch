package scratch.exception;

@SuppressWarnings("serial")
public class MailException extends Exception{

	public MailException() {}
	
	public MailException(String message) {
		super(message);
	}
	
	@Override
	public String getMessage() {
		return "·¢ËÍÓÊ¼şÊ§°Ü";
	}
	
}
