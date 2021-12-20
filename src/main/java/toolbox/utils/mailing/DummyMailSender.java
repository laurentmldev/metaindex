package toolbox.utils.mailing;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Laurent ML
 */
public class DummyMailSender implements IEmailSender {
   
	private Log log = LogFactory.getLog(DummyMailSender.class);
	
    public void send(final String username, final String password, String recipientEmail, String title, String message) throws AddressException, MessagingException {
        sendHtml(username, password, recipientEmail, "", "", title, message);
    }
    
    public void send(final String username, final String password, String recipientEmail, 
    		String ccEmail, String cciEmail, String title, String message) throws AddressException, MessagingException {
        sendHtml(username, password, recipientEmail, ccEmail, cciEmail, title, message);
    }

    /**
     * Showing email contents rather than sending it
     */
    public void sendHtml(final String username, final String password, String recipientEmail, 
    					String ccEmail, String cciEmail, String title, String htmlMessage) throws AddressException, MessagingException {
        
    	log.info(  "#####  DummySendMail  #####"
    			+"\n\tTo:"+recipientEmail
    			+"\n\tCc:"+ccEmail
    			+"\n\tCci:"+cciEmail
    			+"\n\tTitle:"+title
    			+"\n\tBody"+htmlMessage.replace("<br>", "\n").replace("<br/>", "\n")
    			+"\n###########################"
				);
        
    }
}