package metaindex.data.userprofile;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.List;

import metaindex.app.control.websockets.users.WsControllerUser.CATALOG_MODIF_TYPE;
import metaindex.data.catalog.CatalogVocabularySet;
import metaindex.data.catalog.ICatalog;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IIdentifiable;


/**
 * Java object container for users DB table.
 * Retrieve also String info of corresponding foreign keys (guilanguage and guitheme).
 * From IIDentifiable : 'name' field is user email
 * @author Laurent ML
 */
public interface ICatalogUser extends IIdentifiable<Integer>
{
		
	public enum USER_CATALOG_ACCESSRIGHTS { NONE, CATALOG_READ, CATALOG_EDIT, CATALOG_ADMIN };
	
	public Boolean isEnabled();
	public String getNickname();
	
	/** return the number of catalogs owned by the user */
    public Integer getCurNbCatalogsCreated();
	
    public ICatalog getCurrentCatalog();
    public void setCurrentCatalog(Integer catalogId);
    public void quitCurrentCatalog() throws DataProcessException;
    
    	//-------- user access rights by catalog
	public void clearUserCatalogsIds();
	public List<Integer> getUserCatalogsIds();
	// User role overrides user catalog access rights
	public USER_CATALOG_ACCESSRIGHTS getUserCatalogAccessRights(Integer catalogId);

	//----------
	public CatalogVocabularySet getCatalogVocabulary();
	
	public void notifyCatalogContentsChanged(CATALOG_MODIF_TYPE modifType, 
																Long nbImpactedItems);
	public void notifyCatalogContentsChanged(CATALOG_MODIF_TYPE modifType, 
								String impactedItemName, String impactDetails);
		
	public List<ICatalog> getOwnedCatalogs();

}
