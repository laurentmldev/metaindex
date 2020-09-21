package metaindex.app.control.websockets.catalogs.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import metaindex.app.Globals;
import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;

/**
 * used for catalogs search, avoiding to transmit (and compute) full data
 * only basic ones are fulfilled.
 * @author laurentml
 *
 */
public class WsMsgCatalogShortDetails_answer extends WsMsgCatalogDetails_answer  {
	
	public WsMsgCatalogShortDetails_answer(ICatalog c, IUserProfileData u) {
		this.setId(c.getId());
		this.setName(c.getName());	
		this.setVocabularies(c.getVocabularies());
		this.setVocabulary(c.getVocabulary(u.getGuiLanguageId()));
		this.setUserAccessRights(u.getUserCatalogAccessRights(c.getId()));
		this.setUserAccessRightsStr(u.getText("Profile.userAccessRights."+this.getUserAccessRights().toString()));
		
	}	
	
}