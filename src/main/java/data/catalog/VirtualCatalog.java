package metaindex.data.catalog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityTerm;
import metaindex.data.element.Element;
import metaindex.data.element.ElementHandle;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.accessors.ACatalogAccessor;
import metaindex.dbaccess.accessors.ADatasetAccessor;
import metaindex.dbaccess.accessors.AElementAccessor;
import metaindex.dbaccess.accessors.AMetadataAccessor;
import metaindex.dbaccess.accessors.AMetaindexAccessor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Bean storing community data
 * @author laurent
 *
 */
public abstract class VirtualCatalog extends Catalog {

	private Log log = LogFactory.getLog(VirtualCatalog.class);
	private static boolean show_remove_el_from_virtual=true;

	public VirtualCatalog(Community myCommunity) {
		super(myCommunity);
		
	}
	

	
	@Override
  	public void setCatalogId(int catalogID) {
		// disabled for such a virtual catalog
  		log.error("Warning : trying to setCatalogId of a virtual catalog");
	}
	
  	@Override
  	public boolean isIdentified() {
  		return this.getCatalogId()>0;
  	}
	
  	@Override
	public void delete(IUserProfileData activeUser)
			throws DataAccessErrorException,DataAccessConstraintException {  		
  		// disabled for such a virtual catalog
  		log.error("Warning : trying to delete a virtual catalog");
  	}
	
	@Override
	public void commit(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException {
  		// disabled for such a virtual catalog
  		log.error("Warning : trying to commit a virtual catalog");
			
	}
	@Override
	public void commitFull(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException {
  		// disabled for such a virtual catalog
  		log.error("Warning : trying to commitFull a virtual catalog");
			
	}
		
	@Override
	public void create(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
  		// disabled for such a virtual catalog
  		log.error("Warning : trying to create a virtual catalog");
	}
	@Override
	public void update(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException {
  		// disabled for such a virtual catalog  			
	}

	public void addStaticElement (IUserProfileData activeUser, int elementToAddId) throws DataAccessErrorException, DataAccessConstraintException 
	{
  		// disabled for such a virtual catalog
  		log.error("Warning : trying to addStaticElement to a virtual catalog");	
	}

	public void removeStaticElement (IUserProfileData activeUser, int elementToAddId) throws DataAccessErrorException, DataAccessConstraintException 
	{
  		// disabled for such a virtual catalog
		if (show_remove_el_from_virtual) { 
	  		log.warn("Warning : trying to removeStaticElement from a virtual catalog");
	  		show_remove_el_from_virtual=false;
		}
	}
	
	public boolean isVirtual() {
		return true;
	}

	public void setVirtual(boolean isVirtual) {
  		// disabled for such a virtual catalog
  		log.error("Warning : trying to setVirtual a virtual catalog");
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public void setSearchQuery(String searchQuery) {
		// disabled for such a virtual catalog
  		log.error("Warning : trying to setSearchQuery of a virtual catalog");
	}


}
