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
public class MetadataHandle_Image extends AMetadataHandle_Specialized<IMetadata_Image> implements IMetadata_Image  {
	
	private Log log = LogFactory.getLog(Metadata_Image.class);
	

	public MetadataHandle_Image(IMetadata_Image metadata) {
		super(metadata);	
	}
	
	@Override
	public String getImageUrl() { return getRefMetadata().getImageUrl(); }
	@Override
	public void setImageUrl(String imageLink) { getRefMetadata().setImageUrl(imageLink); }
	
	@Override
	public boolean isThumbnail() { return getRefMetadata().isThumbnail(); }
	@Override
	public void setThumbnail(boolean val) { this.getRefMetadata().setThumbnail(val); }
	
	@Override
	public void setBorderSize(Long borderSize) { getRefMetadata().setBorderSize(borderSize); }
	@Override
	public Long getBorderSize() {  return getRefMetadata().getBorderSize(); }
	
	@Override
	public String getBorderColor() { return this.getRefMetadata().getBorderColor(); }
	@Override
	public void setBorderColor(String color) { this.getRefMetadata().setBorderColor(color) ;}
		
	@Override
	public String getSearchText() { return getRefMetadata().getSearchText(); }

	@Override
	public void setValueFromStr(String str) throws InapropriateStringForTypeException {
		this.getRefMetadata().setValueFromStr(str);		
	}
}
