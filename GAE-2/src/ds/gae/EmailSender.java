package ds.gae;

import java.util.Date;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

	public static void sendMail(String subject, String body) {
		String host = "smtp.google.com";
		String to = "example@yahoo.fr";
		String from = "example@gmail.com";
		boolean sessionDebug = false;
		
		// Create some properties and get the default Session.
		Properties props = System.getProperties();
		props.put("mail.host", host);
		props.put("mail.transport.protocol", "smtp");
		Session mailSession = Session.getDefaultInstance(props, null);

		// Set debug on the Session
		// Passing false will not echo debug info, and passing True will.

		mailSession.setDebug(sessionDebug);

		// Instantiate a new MimeMessage and fill it with the 
		// required information.

		MimeMessage msg = new MimeMessage(mailSession);
		try {
			msg.setFrom(new InternetAddress(from));
			InternetAddress[] address = { new InternetAddress(to) };
			msg.setRecipients(MimeMessage.RecipientType.TO, address);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			msg.setText(body);

			// Hand the message to the default transport service
			// for delivery.

			Transport.send(msg);
		} catch (AddressException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
}
