package toolbox.utils.mailing;

import com.sun.mail.smtp.SMTPTransport;

import metaindex.app.periodic.statistics.MxStatisticsManager;

import java.security.Security;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author doraemon / Laurent ML
 */
public class GoogleMailSender implements IEmailSender {
   
	private Log log = LogFactory.getLog(GoogleMailSender.class);
	
    /**
     * Send email using GMail SMTP server.
     *
     * @param username GMail username
     * @param password GMail password
     * @param recipientEmail TO recipient
     * @param title title of the message
     * @param message message to be sent
     * @throws AddressException if the email address parse failed
     * @throws MessagingException if the connection is dead or not in the connected state or if the message is not a MimeMessage
     */
	@Override
    public void send(final String username, final String password, String recipientEmail, String title, String message) throws AddressException, MessagingException {
        sendHtml(username, password, recipientEmail, "", "", title, message);
    }
    
    @Override
    public void send(final String username, final String password, String recipientEmail, 
    							String ccEmail, String cciEmail, String title, String message) throws AddressException, MessagingException {
        sendHtml(username, password, recipientEmail, ccEmail, cciEmail, title, message);
    }

    /**
     * Send email using GMail SMTP server.
     *
     * @param username GMail username
     * @param password GMail password
     * @param recipientEmail TO recipient
     * @param ccEmail CC recipient. Can be empty if there is no CC recipient
     * @param title title of the message
     * @param htmlMessage message to be sent
     * @throws AddressException if the email address parse failed
     * @throws MessagingException if the connection is dead or not in the connected state or if the message is not a MimeMessage
     */
    @Override
    public void sendHtml(final String username, final String password, String recipientEmail, 
    							String ccEmail, String cciEmail, String title, String htmlMessage) throws AddressException, MessagingException {
        
    	Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        // Get a Properties object
        Properties props = System.getProperties();
        props.setProperty("mail.smtps.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.setProperty("mail.smtps.auth", "true");

        /*
        If set to false, the QUIT command is sent and the connection is immediately closed. If set 
        to true (the default), causes the transport to wait for the response to the QUIT command.

        ref :   http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html
                http://forum.java.sun.com/thread.jspa?threadID=5205249
                smtpsend.java - demo program from javamail
        */
        props.put("mail.smtps.quitwait", "true");

        Session session = Session.getInstance(props, null);

        // -- Create a new message --
        final MimeMessage msg = new MimeMessage(session);

        // -- Set the FROM and TO fields --
        msg.setFrom(new InternetAddress(username + "@gmail.com"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));

        if (ccEmail.length() > 0) {
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
        }
        if (cciEmail.length() > 0) {
            msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(cciEmail, false));
        }
        msg.setSubject(title);
        Multipart multipart = new MimeMultipart( "alternative" );
/*
        MimeBodyPart textPart = new MimeBodyPart();
        String textMessage=htmlMessage.replaceAll("<br/>","\n");
        textPart.setText(textMessage, "utf-8" );
*/
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent( htmlMessage, "text/html; charset=utf-8" );
        htmlPart.setHeader("Content-Type", "text/html");

  //      multipart.addBodyPart( textPart );
        multipart.addBodyPart( htmlPart );
        msg.setContent( multipart );
        
        msg.setSentDate(new Date());
        msg.saveChanges();
        SMTPTransport t = (SMTPTransport)session.getTransport("smtps");

        t.connect("smtp.gmail.com", username, password);
        t.sendMessage(msg, msg.getAllRecipients());      
        t.close();
        
    }
}