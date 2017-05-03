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

import scratch.exception.MailException;
import scratch.service.MailTemplate;
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
	private List<String> templates;
	private List<String> templatePaths;
	private List<String> subjects;
	
	public void sendMail(MailTemplate template, String toMail, Map<String, Object> map) throws MailException, MessagingException {
		//判断模板是否存在
		int index = templates.indexOf(template.toString());
		if(index < 0) {
			throw new MailException("邮件模板不存在");
		}
		//获取模板文件路径、模板主题
		String templatePath = templatePaths.get(index);
		String subject = subjects.get(index);
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

	public List<String> getTemplates() {
		return templates;
	}

	public void setTemplates(List<String> templates) {
		this.templates = templates;
	}

	public List<String> getTemplatePaths() {
		return templatePaths;
	}

	public void setTemplatePaths(List<String> templatePaths) {
		this.templatePaths = templatePaths;
	}

	public List<String> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}
	
}
