package metaindex.websockets.commons;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sun.org.apache.xml.internal.security.utils.Base64;

import metaindex.data.catalog.ICatalog;
import metaindex.data.commons.globals.Globals;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.IUserProfileData.USER_CATALOG_ACCESSRIGHTS;
import metaindex.data.userprofile.IUserProfileData.USER_ROLE;
import metaindex.websockets.catalogs.WsControllerCatalog;
import toolbox.exceptions.DataProcessException;



public class AMxWSController {
	
	private Log log = LogFactory.getLog(AMxWSController.class);
	
	public AMxWSController(SimpMessageSendingOperations messageSender) {
		this.messageSender = messageSender;		
	}		
	
	protected byte[] compressString(final String data, final String encoding)
		    throws IOException
		{
		    if (data == null || data.length() == 0)
		    {
		        return null;
		    }
		    else
		    {
		        byte[] bytes = data.getBytes(encoding);
		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        GZIPOutputStream os = new GZIPOutputStream(baos);
		        os.write(bytes, 0, bytes.length);
		        os.close();
		        byte[] result = baos.toByteArray();
		        return result;
		    }
		}
	
	protected String uncompressString(final byte[] data, final String encoding)
		    throws IOException
		{
		    if (data == null || data.length == 0)
		    {
		        return null;
		    }
		    else
		    {
		        ByteArrayInputStream bais = new ByteArrayInputStream(data);
		        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		        GZIPInputStream is = new GZIPInputStream(bais);
		        byte[] tmp = new byte[256];
		        while (true)
		        {
		            int r = is.read(tmp);
		            if (r < 0)
		            {
		                break;
		            }
		            buffer.write(tmp, 0, r);
		        }
		        is.close();

		        byte[] content = buffer.toByteArray();
		        return new String(content, 0, content.length, encoding);
		    }
		}
	
	protected IUserProfileData getUserProfile(SimpMessageHeaderAccessor headerAccessor) throws DataProcessException {
		
		Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();    		
		String httpSessionId=sessionAttributes.get("HTTP.SESSION.ID").toString();
    	
		IUserProfileData user = Globals.Get().getUsersMgr().getUserByHttpSessionId(httpSessionId);
		if (user==null || !user.isLoggedIn()) {
			user = Globals.Get().getUsersMgr().getUserByName(headerAccessor.getUser().getName());
			if (user == null) {
				throw new DataProcessException("No such user '"+headerAccessor.getUser().getName()+"' found (sessionId="+httpSessionId+").");
			}
			user.quitCurrentCatalog();
			user.setHttpSessionId(httpSessionId);
		}
		if (!user.isLoggedIn()) {
			throw new DataProcessException("User '"+headerAccessor.getUser().getName()+"' is not currently logged in, operation refused.");
		}
		return user;
	}
	
	protected SimpMessageSendingOperations messageSender;	
	
	protected String getCompressedString(String str) throws IOException  {
		return Base64.encode(compressString(str,"UTF-8"));		
	}
	
	protected String getCompressedRawString(Object object) throws IOException  {
		ObjectMapper mapper = new ObjectMapper();
		String txtJsonToSend = mapper.writeValueAsString(object);
		String compressedMsg = getCompressedString(txtJsonToSend);
		return compressedMsg;
	}
	protected String getRawString(Object object) throws IOException  {
		ObjectMapper mapper = new ObjectMapper();
		String txtJsonToSend = mapper.writeValueAsString(object);
		return txtJsonToSend;
	}
	protected void sendListToUser(String userName, String wsQueueName, 
										@SuppressWarnings("rawtypes") List answeredList,
										Boolean compressWithBase64)
			throws IOException {
		
		if (!compressWithBase64) {
			this.messageSender.convertAndSendToUser(userName,wsQueueName, getRawString(answeredList));
		} else {
			this.messageSender.convertAndSendToUser(userName,wsQueueName, getCompressedRawString(answeredList));
		}
		
    }    
  
	protected void sendMsgToUser(String userName, String wsQueueName, 
			Object answeredMsg,
			Boolean compressWithBase64) throws IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		String txtJsonToSend = mapper.writeValueAsString(answeredMsg);	
		
		if (!compressWithBase64) {
			this.messageSender.convertAndSendToUser(userName,wsQueueName, txtJsonToSend);
		}
		
		else {
			String compressedMsg = Base64.encode(compressString(txtJsonToSend,"UTF-8"));
			this.messageSender.convertAndSendToUser(userName,wsQueueName, compressedMsg);
		}		
	}
	
	
	protected Boolean userHasReadAccess(IUserProfileData user) {
		return 		user.isEnabled() &&
				(
				user.getRole()==USER_ROLE.ROLE_OBSERVER
				||	user.getRole()==USER_ROLE.ROLE_USER
				||	user.getRole()==USER_ROLE.ROLE_ADMIN);
	}
	
	protected Boolean userHasWriteAccess(IUserProfileData user) {
		return 		user.isEnabled() &&
				(user.getRole()==USER_ROLE.ROLE_USER
				||	user.getRole()==USER_ROLE.ROLE_ADMIN);
	}
	protected Boolean userHasAdminAccess(IUserProfileData user) {
		return 	user.getRole()==USER_ROLE.ROLE_ADMIN;
	}

	protected Boolean userHasReadAccess(IUserProfileData user, ICatalog cat) {
		return 		user.isEnabled() &&
				(user.getUserCatalogAccessRights(cat.getId())==USER_CATALOG_ACCESSRIGHTS.CATALOG_READ
				||	user.getUserCatalogAccessRights(cat.getId())==USER_CATALOG_ACCESSRIGHTS.CATALOG_EDIT
				||  user.getUserCatalogAccessRights(cat.getId())==USER_CATALOG_ACCESSRIGHTS.CATALOG_ADMIN);
	}
	
	protected Boolean userHasWriteAccess(IUserProfileData user, ICatalog cat) {
		return 		user.isEnabled() &&
				(user.getUserCatalogAccessRights(cat.getId())==USER_CATALOG_ACCESSRIGHTS.CATALOG_EDIT
				||  user.getUserCatalogAccessRights(cat.getId())==USER_CATALOG_ACCESSRIGHTS.CATALOG_ADMIN);
	}
	
	protected Boolean userHasAdminAccess(IUserProfileData user, ICatalog cat) {
		return 		user.isEnabled() &&
				(user.getUserCatalogAccessRights(cat.getId())==USER_CATALOG_ACCESSRIGHTS.CATALOG_ADMIN);
	}
}
