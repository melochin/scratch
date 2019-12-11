package scratch.support.service;

import java.io.Serializable;
import java.util.Map;

public class MailContent implements Serializable {

	private String subject;

	private String templateName;

	private String receiverEmail;

	private Map<String, Object> data;

	public MailContent(String subject, String templateName, String receiverEmail, Map<String, Object> data) {
		this.subject = subject;
		this.templateName = templateName;
		this.receiverEmail = receiverEmail;
		this.data = data;
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

	public String getReceiverEmail() {
		return receiverEmail;
	}

	public void setReceiverEmail(String receiverEmail) {
		this.receiverEmail = receiverEmail;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
}
