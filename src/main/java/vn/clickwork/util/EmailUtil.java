package vn.clickwork.util;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {
	
	private JavaMailSender emailSender;
	
	public EmailUtil(JavaMailSender emailSender) {
		this.emailSender = emailSender;
	}
	
	public void sendEmail(String to, String subject, String text) {

		 SimpleMailMessage message = new SimpleMailMessage();
		 message.setTo(to);
		 message.setSubject(subject);
		 message.setText(text);
		 emailSender.send(message);
		
		System.out.println("Sending email to: " + to);
		System.out.println("Subject: " + subject);
		System.out.println("Text: " + text);
		

	}

}
