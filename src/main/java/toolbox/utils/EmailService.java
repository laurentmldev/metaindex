package toolbox.utils;

import java.util.Properties;
import java.util.concurrent.Semaphore;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import metaindex.data.commons.globals.Globals;
import metaindex.data.userprofile.UserProfileData;
import toolbox.exceptions.DataProcessException;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/



public class EmailService {
	static private Log log = LogFactory.getLog(EmailService.class);
    private JavaMailSender _mailSender; // Defined as a bean in XML application context
    
    static private Semaphore _mailSenderLock = new Semaphore(1,true);
    static public void acquireLock() throws InterruptedException { _mailSenderLock.acquire(); }
    static public void releaseLock() { _mailSenderLock.release(); }
    static private EmailService _Singleton=null;
    
    private EmailService()
    {
	/*
			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		    mailSender.setHost(Globals.GetMxProperty("mx.mailer.host"));
		    mailSender.setPort(new Integer(Globals.GetMxProperty("mx.mailer.port")));
		     
		    mailSender.setUsername(Globals.GetMxProperty("mx.mailer.user"));
		    mailSender.setPassword(Globals.GetMxProperty("mx.mailer.password"));
		     */
    	
    	/*
		    Properties properties = new Properties();//mailSender.getJavaMailProperties();
		    properties.put("mail.transport.protocol", "smtp");
		    properties.put("mail.smtp.auth", "true");
		    properties.put("mail.smtp.starttls.enable", "true");
		    properties.put("mail.debug", "true");
		    properties.put("mail.smtp.host", Globals.GetMxProperty("mx.mailer.host"));
	        properties.put("mail.smtp.port", Globals.GetMxProperty("mx.mailer.port"));
		    
		    Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(	Globals.GetMxProperty("mx.mailer.user"), 
	                									Globals.GetMxProperty("mx.mailer.password"));
	            }
	        });
		    
		    try
	        {
	            // Create MimeMessage object
	            MimeMessage message = new MimeMessage(session);

	            // Set the Senders mail to From
	            message.setFrom(new InternetAddress("laurentmlcontact@google.com"));

	            // Set the recipients email address
	            message.addRecipient(Message.RecipientType.TO, new InternetAddress("laurentmlcontact-metaindex@yahoo.fr"));

	            // Subject of the email
	            message.setSubject("Java Send Email Gmail SMTP with TLS Authentication");

	            // Body of the email
	            message.setText("Welcome to Java Interviewpoint");

	            // Send email.
	            Transport.send(message);
	            System.out.println("Mail sent successfully");
	        } catch (MessagingException e)
	        {
	            e.printStackTrace();
	        }
	    
		    //_mailSender=mailSender;
		 
		 */
    	
    	try {
			GoogleMail.Send("account", "secret","dest@test.com", "[MX] Welcome!", "Helololo!");
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
   
    private JavaMailSender getMailSender() { return _mailSender; }
 
    /**
     * This method will compose and send the message 
     * */
    static public void SendMail(String to, String subject, String body) throws DataProcessException
    {
    	
    	
    	try {
			_mailSenderLock.acquire();
			if (_Singleton==null) {
	    		_mailSenderLock.release();
	    		_Singleton=new EmailService();
	    	}
	    	_mailSenderLock.release();
	    	
	    	/*
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(to);
	        message.setSubject(subject);
	        message.setText(body);
	        _Singleton.getMailSender().send(message);
	        */
	        
    	} catch (InterruptedException e) { e.printStackTrace(); } 
    	catch (Exception e2) {
    		log.error("Unable to send email to "+to+" : "+e2.getMessage());
    		
		}
    	
    }
 
}
