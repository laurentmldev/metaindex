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
public class Metadata_WebLink extends AMetadata_Specialized implements IMetadata_WebLink {
	
	private Log log = LogFactory.getLog(Metadata_WebLink.class);
	
	public Metadata_WebLink(IMetadata metadata) {
		super(metadata);		
	}
	@Override
	public String getUrl() { return getRefMetadata().getString1(); }
	@Override
	public void setUrl(String url) throws DataAccessConstraintException{ getRefMetadata().setString1(url); }
	@Override
	public String getLinkText() { return getRefMetadata().getString2(); }
	@Override
	public void setLinkText(String linkText) throws DataAccessConstraintException { getRefMetadata().setString2(linkText); }
		
	@Override
	public String getSearchText() {
		return getRefMetadata().getString1() + " " + getRefMetadata().getString2();
	}
	
	@Override
	public void setValueFromStr(String str) throws InapropriateStringForTypeException {
		try {
			this.setUrl(str);
		} catch (Exception e) { throw new InapropriateStringForTypeException(e.getMessage()); }		
	}
}
