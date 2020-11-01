package metaindex.data.catalog;

import java.util.List;

import metaindex.data.userprofile.IUserProfileData;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


/**
 * Bean storing catalog data
 * @author laurent
 *
 */
public interface ICatalogChat {
	List<ICatalogChatMsg> getChatHistory();
	void postMessage(IUserProfileData postingUser,ICatalogChatMsg msg);	
}
