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
public class EmailSupport {

	private JavaMailSender mailSender;
	
	private TemplateEngine templateEngine;

	public void sendMail(MailContent mailContent) throws MailException, MessagingException {
		String subject = mailContent.getSubject();
		String templateName = mailContent.getTemplateName();
		//读取模板
		String content = templateEngine.getContent(templateName, mailContent.getData());
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper messHelper = new MimeMessageHelper(message);
		
		messHelper.setTo(mailContent.getReceiverEmail());
		messHelper.setSubject(subject);
		messHelper.setText(content, true);
		
		mailSender.send(message);
	}

	public void sendMail(String subject, String content, String toMail) throws MailException, MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper messHelper = new MimeMessageHelper(message);

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
