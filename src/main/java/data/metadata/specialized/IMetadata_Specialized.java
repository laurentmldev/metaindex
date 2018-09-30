package metaindex.data.metadata.specialized;

import metaindex.data.metadata.IMetadataFunctions.InapropriateStringForTypeException;

/**
 * Bean storing community data
 * @author laurent
 *
 */
public interface IMetadata_Specialized {
	
	public String getSearchText();
	
	public void setValueFromStr(String str) throws InapropriateStringForTypeException;
	
}
