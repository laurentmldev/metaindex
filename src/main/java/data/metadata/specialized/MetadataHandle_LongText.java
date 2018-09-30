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
public class MetadataHandle_LongText extends AMetadataHandle_Specialized<IMetadata_LongText> implements IMetadata_LongText {
	
	private Log log = LogFactory.getLog(Metadata_LongText.class);
	
	
	public MetadataHandle_LongText(IMetadata_LongText metadata) {
		super(metadata);		
	}
	
	
	@Override
	public String getText() { return getRefMetadata().getText(); }
	@Override
	public void setText(String tinyText) { getRefMetadata().setText(tinyText); }
	@Override
	public String getSearchText() { return getRefMetadata().getSearchText(); }

	@Override
	public void setValueFromStr(String str) throws InapropriateStringForTypeException {
		this.getRefMetadata().setValueFromStr(str);		
	}
	
	
}
