package scratch.support.service;

@SuppressWarnings("serial")
public class MailException extends Exception{

	public MailException() {}
	
	public MailException(String message) {
		super(message);
	}
	
}
