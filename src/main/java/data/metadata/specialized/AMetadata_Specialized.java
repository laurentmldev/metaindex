package metaindex.data.metadata.specialized;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.dataset.IDataset;
import metaindex.data.dataset.IDatasetContents;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementContents;
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.IMetadataContents;
import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.metadata.MetadataHandle;
import metaindex.data.metadata.IMetadataFunctions.InapropriateStringForTypeException;

/**
 * Generic parent class for specialized handles over a given metadata object
 * @author laurent
 *
 */
public abstract class AMetadata_Specialized implements IMetadata_Specialized {
	
	private Log log = LogFactory.getLog(AMetadata_Specialized.class);
	private IMetadata myMetadata=null;
	public AMetadata_Specialized(IMetadata metadata) {
		myMetadata=metadata;		
	}
	
	protected IMetadata getRefMetadata() {
		return myMetadata; 
	}
	

}
