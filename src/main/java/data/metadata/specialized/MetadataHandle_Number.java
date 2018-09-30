package metaindex.data.metadata.specialized;

import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.community.ACommunitySubdata;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityTerm;
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.IMetadataContents;
import metaindex.data.metadata.IMetadataFunctions.InapropriateStringForTypeException;
import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.metadata.MetadataHandle;
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
public class MetadataHandle_Number extends AMetadataHandle_Specialized<IMetadata_Number> implements IMetadata_Number  {
	
	private Log log = LogFactory.getLog(Metadata_Image.class);
	

	public MetadataHandle_Number(IMetadata_Number metadata) {
		super(metadata);	
	}
	

		
	@Override
	public String getSearchText() { return getRefMetadata().getSearchText(); }

	@Override
	public void setValueFromStr(String str) throws InapropriateStringForTypeException {
		this.getRefMetadata().setValueFromStr(str);		
	}



	@Override
	public Double getValue() {
		return this.getRefMetadata().getValue();
	}



	@Override
	public void setValue(Double newVal) {
		this.getRefMetadata().setValue(newVal);		
	}



	@Override
	public String getUnit() {
		return this.getRefMetadata().getUnit();
	}



	@Override
	public void setUnit(String unit) {
		this.getRefMetadata().setUnit(unit);
	}
}
