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
public class Metadata_LongText extends AMetadata_Specialized implements IMetadata_LongText {
	
	private Log log = LogFactory.getLog(Metadata_LongText.class);
	
	
	public Metadata_LongText(IMetadata metadata) {
		super(metadata);		
	}
	
	
	@Override
	public String getText() { return getRefMetadata().getLongString(); }
	@Override
	public void setText(String tinyText) throws DataAccessConstraintException { getRefMetadata().setLongString(tinyText); }
	
	@Override
	public String getSearchText() {
		return getRefMetadata().getLongString();
	}

	@Override
	public void setValueFromStr(String str) throws InapropriateStringForTypeException {
		try {
			this.setText(str);
		} catch (Exception e) { throw new InapropriateStringForTypeException(e.getMessage()); }		
	}
	
	
}
