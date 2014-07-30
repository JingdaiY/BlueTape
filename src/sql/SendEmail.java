package sql;

//http://stackoverflow.com/questions/3649014/send-email-using-java/14973045#14973045
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

//to import javax.mail you have to download external JAR

public class SendEmail
	{
	public static void send(String to, String from, String inPassword, String subject, String text) throws AddressException, MessagingException
		{

		// String to = "jd@yahoo.com";
		// String from = "jdy@yahoo.com";
		// String host = "localhost";
		Properties properties = System.getProperties();
		// properties.setProperty("mail.smtp.host", "smtp.mail.yahoo.com");
		properties.setProperty("mail.smtp.host", "smtp.gmail.com");
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.smtp.port", "587");
		
		Session session=Session.getInstance(properties, new SendEmail.myAuthenticator(from, inPassword));
		
		try
			{
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);
			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));
			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					to));
			// Set Subject: header field
			message.setSubject(subject);
			// Now set the actual message
			message.setText(text);
			// Send message
			Transport.send(message);
			System.out.println("Sent message successfully....");
			}
		catch (MessagingException mex)
			{
			mex.printStackTrace();
			throw mex;
			}
		}
	static class myAuthenticator extends Authenticator
		{
		String emailAddress;
		String emailPassword;
		public myAuthenticator(String inEmailAddress, String inEmailPassword)
			{
			super();
			emailAddress=inEmailAddress;
			emailPassword=inEmailPassword;
			}
		@Override
		protected PasswordAuthentication getPasswordAuthentication()
			{
			return new PasswordAuthentication(emailAddress, emailPassword);
			//return new PasswordAuthentication("bluetape.usermanagement@gmail.com", "chenjingdao");
			}
		}
	public static void main(String[] args)
		{
		if(args.length!=5)
			{
			System.out.println("Usage: <to> <from> <from_password> <subject> <text>");
			}
		else
			{
			try
				{
				send(args[0], args[1], args[2], args[3], args[4]);
				}
			catch (AddressException e)
				{
				e.printStackTrace();
				}
			catch (MessagingException e)
				{
				e.printStackTrace();
				}
			}
		}
	}
