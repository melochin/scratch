package scratch.support.service;

import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * 邮件发送服务
 * @author melochin
 *
 */
public class MailService {

	private JavaMailSender mailSender;
	
	private TemplateEngine templateEngine;
	
	public void sendMail(MailContent mailContent, String toMail, Map<String, Object> map) throws MailException, MessagingException {
		String subject = mailContent.getSubject();
		String templateName = mailContent.getTemplateName();
		//读取模板
		String content = templateEngine.getContent(templateName, map); 
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper messHelper = new MimeMessageHelper(message);
		
		System.out.println(content);
		messHelper.setFrom("398299262@qq.com");
		messHelper.setTo(toMail);
		messHelper.setSubject(subject);
		messHelper.setText(content, true);
		
		mailSender.send(message);
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void setTemplateEngine(TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

}
