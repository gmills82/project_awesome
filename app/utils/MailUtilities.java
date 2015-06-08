package utils;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * User: grant.mills
 * Date: 6/8/15
 * Time: 11:33 AM
 */
public class MailUtilities {
	private static Properties fMailServerConfig = new Properties();


	static {
		fMailServerConfig.setProperty("mail.debug", "true");
		fMailServerConfig.setProperty("mail.smtp.host", "smtp.1and1.com");
		fMailServerConfig.setProperty("mail.smtp.port", "587");
		fMailServerConfig.setProperty("mail.smtp.starttls.enable", "true");
		fMailServerConfig.setProperty("mail.smtp.auth", "true");
	}

	/**
	 * Send a single email.
	 */
	public void sendEmail(String aFromEmailAddr, String aToEmailAddr, String aSubject, String aBody) {
		Session session = Session.getDefaultInstance(fMailServerConfig, new SMTPAuthenticator());
		MimeMessage message = new MimeMessage(session);
		try {
			//the "from" address may be set in code, or set in the
			//config file under "mail.from" ; here, the latter style is used
			message.addRecipient(
				Message.RecipientType.TO, new InternetAddress(aToEmailAddr)
			);
			message.setSubject(aSubject);
			message.setFrom(new InternetAddress(aFromEmailAddr));
			message.setText(aBody);
			Transport.send(message);
		} catch (MessagingException ex) {
			System.err.println("Cannot send email. " + ex);
		}
	}

	private class SMTPAuthenticator extends javax.mail.Authenticator {
		private String username = "admin@efsmanager.com";
		private String password = "Pheonix0";

		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username, password);
		}
	}
}
