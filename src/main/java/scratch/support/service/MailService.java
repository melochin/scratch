package scratch.support.service;

import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.velocity.VelocityEngineUtils;
/**
 * 邮件发送服务
 * @author melochin
 *
 */
public class MailService {

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private VelocityEngine velocityEngine;
	
	//模板名 模板路径 主题名
	private List<MailTemplate> templates;
	
	
	public MailTemplate findTemplate(String name) throws MailException {
		if(templates == null || templates.size() == 0) {
			throw new MailException("dont have any mail template");
		}
		for(MailTemplate template : templates) {
			if(name.equals(template.getName())) {
				return template;
			}
		}
		throw new MailException("cant find mail template[" + name + "]");
	}
	
	public void sendMail(String templateName, String toMail, Map<String, Object> map) throws MailException, MessagingException {
		//判断模板是否存在
		MailTemplate template = findTemplate(templateName);
		//获取模板文件路径、模板主题
		String templatePath = template.getPath();
		String subject = template.getSubject();
		
		//读取模板
		String content = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, 
				templatePath, "utf-8", map);
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper messHelper = new MimeMessageHelper(message);
		
		//messHelper.setFrom("398299262@qq.com");
		messHelper.setTo(toMail);
		messHelper.setSubject(subject);
		messHelper.setText(content, true);
		
		mailSender.send(message);
	}

	public JavaMailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}

	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	public List<MailTemplate> getTemplates() {
		return templates;
	}

	public void setTemplates(List<MailTemplate> templates) {
		this.templates = templates;
	}

}
