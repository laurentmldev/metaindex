package metaindex.app.filters;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.userdetails.User;

import metaindex.app.Globals;
import metaindex.app.beans.AMetaindexBean;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.IUserProfileData.USER_CATALOG_ACCESSRIGHTS;

import javax.servlet.http.HttpServletRequest;

/**
 * Manages authorization to the system.
 *
 * @author Michael Klaene
 */
public class CatalogDataAccessControlFilter implements Filter  {
   
	private Log log = LogFactory.getLog(AMetaindexBean.class);
	
	private String extractCatalogNameFromUri(String uri) {		
		String catalogName=uri.replace(Globals.LOCAL_USERDATA_PATH_SUFFIX, "").replaceAll("/.*", "");
		return catalogName;
	}
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
		
		HttpSession session = ((HttpServletRequest)request).getSession(false);
		if (session==null) { throw new IOException("No access right for this URI."); }
		String sessionId=session.getId();
		
		IUserProfileData user=Globals.Get().getUsersMgr().getUserByHttpSessionId(sessionId);	 
		if (user==null) { throw new IOException("No access right for this URI, user unknown."); }
				
		String URI = ((HttpServletRequest)request).getRequestURI();
		String catalogName = extractCatalogNameFromUri(URI);
		
		ICatalog c = Globals.Get().getCatalogsMgr().getCatalog(catalogName);
		if (c==null) { throw new IOException("No access right for this URI, no such contents."); }
		if (user.getUserCatalogAccessRights(c.getId())==USER_CATALOG_ACCESSRIGHTS.NONE) {
			throw new IOException("Insufficient access rights for this URI.");
		}
		 		
		chain.doFilter(request,response);
		
	}
		
}

