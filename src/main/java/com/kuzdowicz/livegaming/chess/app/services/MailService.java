package com.kuzdowicz.livegaming.chess.app.services;

import java.io.IOException;
import java.util.Properties;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.kuzdowicz.livegaming.chess.app.constants.MailSubject;
import com.kuzdowicz.livegaming.chess.app.props.ChessAppProperties;

@Service
public class MailService {

	private final Logger logger = Logger.getLogger(MailService.class);
	
	

	private JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

	private static Resource resource = new ClassPathResource("/chessApp.properties");

	private void prepareMailSender() {

		Properties properties = new Properties();
		try {
			properties = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException e) {
			logger.debug(e);
		}

		mailSender.setUsername(properties.getProperty("mail.username"));
		mailSender.setPassword(properties.getProperty("mail.password"));
		mailSender.setHost(properties.getProperty("smtp.host"));
		mailSender.setPort(Integer.parseInt(properties.getProperty("mail.port")));
		mailSender.setProtocol(properties.getProperty("mail.transport.protocol"));
		mailSender.setJavaMailProperties(properties);
	}

	private String prepareRegistrationMailText(String randomHashForLink, String username) {

		StringBuilder sb = new StringBuilder();
		String siteName = ChessAppProperties.getProperty("domain.name");
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
		String domainName = ChessAppProperties.getProperty("domain.link");
		String confirmEmailLink = domainName + "/registration/confirm/" + hash;
		sb.append(confirmEmailLink);
		sb.append("\">");
		sb.append("click here to confirm your account");
		sb.append("</a>");
		System.out.println(sb.toString());
		return sb.toString();
	}

	public void sendMail(String to, String from, String subject, String messageContent) {
		logger.debug("sendMail()");

		prepareMailSender();

		MimeMessage mimeMessage = mailSender.createMimeMessage();

		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");

		try {
			mimeMessageHelper.setTo(new InternetAddress(to));
			mimeMessageHelper.setFrom(new InternetAddress(from));
			mimeMessageHelper.setSubject(subject);
			mimeMessage.setContent(messageContent, "text/html; charset=utf-8");

		} catch (Exception e) {
			logger.debug(e);
		}

		mailSender.send(mimeMessage);
		logger.debug("send email to " + to);

	}

	public void sendRegistrationMail(String toMailAddress, String username, String randomHashForLink) {

		String fromMailAddress = ChessAppProperties.getProperty("mail.default.message.from");

		sendMail(toMailAddress, fromMailAddress, MailSubject.REGISTRATION_EN,
				prepareRegistrationMailText(randomHashForLink, username));

	}

	// for test
	// public static void main(String[] args) {
	//
	// MailService ms = new MailService();
	// ms.sendRegistrationMail("marcin.kuzdowicz@wp.pl", "test", "test");
	//
	// }

}
