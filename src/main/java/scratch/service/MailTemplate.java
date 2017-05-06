package scratch.service;

public enum MailTemplate {
	REGISTER("register"), RESETPWD("resetpwd"), ANIMEPUSH("animepush");
	
	private String template;
	
	private MailTemplate(String template) {
		this.template = template;
	}
	
	public String toString() {
		return template;
	}

}
