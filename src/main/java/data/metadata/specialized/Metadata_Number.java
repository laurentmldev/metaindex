package metaindex.data.metadata.specialized;

import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.community.ACommunitySubdata;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityTerm;
import metaindex.data.metadata.IMetadata;
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
public class Metadata_Number extends AMetadata_Specialized implements IMetadata_Number {
	
	private Log log = LogFactory.getLog(Metadata_Number.class);
	
	public Metadata_Number(IMetadata metadata) {
		super(metadata);		
	}
	
	private boolean isInteger() {
		return this.getRefMetadata().isValueBoolean1();
	}
	@Override
	public Double getValue() { 
		return this.getRefMetadata().getValueNumber1(); 
	} 
	@Override
	public void setValue(Double newVal) {
		this.getRefMetadata().setValueNumber1(newVal);
	}
	@Override
	public String getSearchText() {
		return this.getValue().toString()+this.getUnit();
	}

	@Override
	public String getUnit() {
		return this.getRefMetadata().getString1();
	}

	@Override
	public void setUnit(String newUnit) {
		this.getRefMetadata().setString1(newUnit);		
	}

	@Override
	public void setValueFromStr(String str) throws InapropriateStringForTypeException {
		try {
			this.setValue(new Double(str));
		} catch (Exception e) { throw new InapropriateStringForTypeException(e.getMessage()); }
	}
	
}
