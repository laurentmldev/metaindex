package toolbox.utils.mailing;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/



public interface IEmailSender {
	
	public void send(String username, String password, 
					 String recipientEmail, String title, String message) throws AddressException, MessagingException;
	
	public void sendHtml(String username, String password, 
			 String recipientEmail, String ccEmail, String title, String message) throws AddressException, MessagingException;
 
}
