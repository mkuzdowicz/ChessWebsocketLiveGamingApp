package com.kuzdowicz.livegaming.chess.app.services;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.kuzdowicz.livegaming.chess.app.constants.MailSubject;

@Service
@PropertySource("classpath:messages.properties")
public class MailService {

	private final static Logger logger = LoggerFactory.getLogger(MailService.class);

	private final JavaMailSender mailSender;
	private final Environment env;

	@Autowired
	public MailService(JavaMailSenderImpl mailSender, Environment env) {
		this.mailSender = mailSender;
		this.env = env;
	}

	private String prepareRegistrationMailText(String randomHashForLink, String username) {

		StringBuilder sb = new StringBuilder();
		String siteName = env.getProperty("domain.name");
		sb.append("<html><head><meta http-equiv=\"Content-Type\" ");
		sb.append("content=\"text/html; charset=UTF-8\"></head><body>");
		sb.append("<p>");
		sb.append("<br />");
		sb.append("Hello, ");
		sb.append("<b>");
		sb.append(username);
		sb.append("</b>");
		sb.append("<br />");
		sb.append("Thanks for signing up with <b>" + siteName + "</b>!");
		sb.append("<br />");
		sb.append("<br />");
		sb.append("<b>You must follow this link</b>, to activate your account: ");
		sb.append("<br />");
		sb.append(prepareRegistrationAnchor(randomHashForLink));
		sb.append("<br />");
		sb.append("Account must be confirmed within a week, otherwise the data will be deleted.");
		sb.append("<br />");
		sb.append("<br />");
		sb.append("Have fun,");
		sb.append("<br />");
		sb.append("The <b>" + siteName + "</b> Team");
		sb.append("<br />");
		sb.append("<br />");
		sb.append("<br />");
		sb.append("</p>");
		sb.append("<p>");
		sb.append("This message was sent automatically from service ");
		sb.append(siteName);
		sb.append("<br />");
		sb.append("If you have not signed up on the site, just ignore this message. ");
		sb.append("</p>");
		sb.append("</body></html>");

		return sb.toString();
	}

	private String prepareRegistrationAnchor(String hash) {
		StringBuilder sb = new StringBuilder();
		sb.append("<a href=\"");
		String domainName = env.getProperty("domain.link");
		String confirmEmailLink = domainName + "/registration/confirm/" + hash;
		sb.append(confirmEmailLink);
		sb.append("\">");
		sb.append("click here to confirm your account");
		sb.append("</a>");
		return sb.toString();
	}

	public void sendMail(String to, String from, String subject, String messageContent) {
		logger.debug("sendMail()");

		MimeMessage mimeMessage = mailSender.createMimeMessage();

		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");

		try {
			mimeMessageHelper.setTo(new InternetAddress(to));
			mimeMessageHelper.setFrom(new InternetAddress(from));
			mimeMessageHelper.setSubject(subject);
			mimeMessage.setContent(messageContent, "text/html; charset=utf-8");

		} catch (Exception e) {
			logger.warn("Exception: ", e);
		}

		mailSender.send(mimeMessage);
		logger.debug("send email to " + to);

	}

	public void sendRegistrationMail(String toMailAddress, String username, String randomHashForLink) {

		String fromMailAddress = env.getProperty("mail.default.message.from");

		sendMail(toMailAddress, fromMailAddress, MailSubject.REGISTRATION_EN,
				prepareRegistrationMailText(randomHashForLink, username));

	}

}
