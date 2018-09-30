package metaindex.data.metadata.specialized;

import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.community.ACommunitySubdata;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityTerm;
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.metadata.MetadataHandle;
import metaindex.data.metadata.IMetadataFunctions.InapropriateStringForTypeException;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.accessors.AMetadataAccessor;
import metaindex.dbaccess.accessors.AMetaindexAccessor;

/**
 * Bean storing community data
 * @author laurent
 *
 */
public class MetadataHandle_WebLink extends AMetadataHandle_Specialized<IMetadata_WebLink> implements IMetadata_WebLink {
	
	private Log log = LogFactory.getLog(IMetadata_WebLink.class);
	
	public MetadataHandle_WebLink(IMetadata_WebLink metadata) {
		super(metadata);		
	}
	
	@Override
	public String getUrl() { return getRefMetadata().getUrl(); }
	@Override
	public void setUrl(String url) { getRefMetadata().setUrl(url); }
	
	@Override
	public String getLinkText() { return getRefMetadata().getLinkText(); }
	@Override
	public void setLinkText(String linkText) { getRefMetadata().setLinkText(linkText); }
	@Override	
	public String getSearchText() { return this.getRefMetadata().getSearchText(); }
	@Override
	public void setValueFromStr(String str) throws InapropriateStringForTypeException {
		this.getRefMetadata().setValueFromStr(str);		
	}
}
