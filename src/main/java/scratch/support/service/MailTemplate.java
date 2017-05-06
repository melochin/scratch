package scratch.support.service;

public class MailTemplate {

	//模板名称
	private String name;
	
	//模板路径
	private String path;
	
	//邮件主题名称
	private String subject;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
}
