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

/**
 * Generic parent class for specialized handles over a given metadata object
 * @author laurent
 *
 */
public abstract class AMetadataHandle_Specialized<T>  {
	
	private Log log = LogFactory.getLog(AMetadata_Specialized.class);
	private T myMetadata=null;
	public AMetadataHandle_Specialized(T metadata) {
		myMetadata=metadata;		
	}
	
	protected T getRefMetadata() {
		return myMetadata; 
	}
	
}
