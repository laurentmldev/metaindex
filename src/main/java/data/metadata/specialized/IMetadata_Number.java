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
public interface IMetadata_Number extends IMetadata_Specialized {
	
	
	public Double getValue();
	public void setValue(Double newVal);
	
	public String getSearchText();
	
	/**
	 * We don't know if this field has been used by another type before being a TinyText' font weight.
	 * So we use a default value if it doesn't match any of the predefined ones.
	 * @return
	 */
	public String getUnit();
	public void setUnit(String unit);
	
	
}
