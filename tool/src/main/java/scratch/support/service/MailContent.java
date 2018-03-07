package scratch.support.service;

public class MailContent {

	private String subject;

	private String templateName;
	
	public MailContent(String subject, String templateName) {
		this.subject = subject;
		this.templateName = templateName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	
}
