package metaindex.data.catalog;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.List;
import java.util.Map;

import metaindex.data.filter.IFilter;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.term.ICatalogTerm.RAW_DATATYPE;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IIdentifiable;
import toolbox.utils.ILockable;

/**
 * Bean storing catalog data
 * @author laurent
 *
 */
public interface ICatalogCustomParams extends IIdentifiable<Integer> {
	
	public void setName(String shortname);
	
	/// fields names (coma-separated) shall be used (concatenated) in order to build the thumbails name
	public List<String> getItemNameFields();
	public void setItemNameFields(List<String> fieldnames);
	
	/// field name to be used to build the items thumbails URLs
	public String getItemThumbnailUrlField();
	public void setItemThumbnailUrlField(String fieldname);
	
	/// to prefix URLs not starting with http
	public String getItemsUrlPrefix();
	public void setItemsUrlPrefix(String urlPrefix);
	
	/// URL to thumbail for the catalog itself
	public String getThumbnailUrl();
	public void setThumbnailUrl(String urlPrefix);

	/// field to be used for matching perspective to document 
	public String getPerspectiveMatchField();
	public void setPerspectiveMatchField(String fieldName);	
			
	public Integer getFtpPort();
}
