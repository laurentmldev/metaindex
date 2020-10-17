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

import metaindex.app.Globals;
import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.ICatalogUser.USER_CATALOG_ACCESSRIGHTS;
import metaindex.data.userprofile.IUserProfileData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Check user access right to catalog drive
 * @author laurentml
 *
 */
public class CatalogDataAccessControlFilter implements Filter  {
   
	private Log log = LogFactory.getLog(CatalogDataAccessControlFilter.class);
	
	private String extractCatalogNameFromUri(String uri) {
		String prefixToRemove="/"+Globals.GetMxProperty("mx.appname")+Globals.LOCAL_USERDATA_PATH_SUFFIX+"/";
		String withoutPrefix=uri.replace(prefixToRemove, "");
		String catalogName=withoutPrefix.replaceAll("/.*", "");
		return catalogName;
	}
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
		
		HttpSession session = ((HttpServletRequest)request).getSession(false);
		if (session==null) { 
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "No access right for this URI");
			return;
		}
		String sessionId=session.getId();
		
		IUserProfileData user=Globals.Get().getUsersMgr().getUserByHttpSessionId(sessionId);	 
		if (user==null) { 			
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "No access right for this URI, rejected user.");
			return;
		}
				
		String URI = ((HttpServletRequest)request).getRequestURI();
		String catalogName = extractCatalogNameFromUri(URI);
		
		ICatalog c = Globals.Get().getCatalogsMgr().getCatalog(catalogName);
		if (c==null) {
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "No access right for this URI, no such catalog.");
			return;
		}
		if (user.getUserCatalogAccessRights(c.getId())==USER_CATALOG_ACCESSRIGHTS.NONE) {
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "No access right for this URI, no such contents.");
			return;
		}
		 		
		chain.doFilter(request,response);
		
	}
		
}

