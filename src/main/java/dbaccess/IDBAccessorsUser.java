package metaindex.dbaccess;

import metaindex.dbaccess.accessors.ACatalogAccessor;
import metaindex.dbaccess.accessors.ACommunityAccessor;
import metaindex.dbaccess.accessors.ACommunityTermsAccessor;
import metaindex.dbaccess.accessors.ADatasetAccessor;
import metaindex.dbaccess.accessors.AElementAccessor;
import metaindex.dbaccess.accessors.AGuiLanguagesAccessor;
import metaindex.dbaccess.accessors.AGuiThemesAccessor;
import metaindex.dbaccess.accessors.AMetadataAccessor;
import metaindex.dbaccess.accessors.AUserProfileAccessor;

public interface IDBAccessorsUser  {
	
	public ACommunityAccessor getCommunityDBAccessor();
	public ACatalogAccessor getCatalogDBAccessor();
	public ACommunityTermsAccessor getCommunityTermsDBAccessor();
	public AElementAccessor getElementDBAccessor();
	public ADatasetAccessor getDatasetDBAccessor();
	public AMetadataAccessor getMetadataDBAccessor();
	public AGuiLanguagesAccessor getGuiLanguagesDBAccessor();
	public AGuiThemesAccessor getGuiThemesDBAccessor();
	public AUserProfileAccessor getUserProfileDBAccessor();

}
