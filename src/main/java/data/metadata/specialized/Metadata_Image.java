package metaindex.data.metadata.specialized;

import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.community.ACommunitySubdata;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityTerm;
import metaindex.data.metadata.CompositeMetadata;
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
public class Metadata_Image extends AMetadata_Specialized implements IMetadata_Image  {
	
	private Log log = LogFactory.getLog(Metadata_Image.class);
	

	public Metadata_Image(IMetadata metadata) {
		super(metadata);	
	}
	
	@Override
	public String getImageUrl() { return getRefMetadata().getLongString(); }
	
	@Override
	public void setImageUrl(String imageLink) throws DataAccessConstraintException { 
		this.getRefMetadata().setLongString(imageLink); 		
		if (this.isThumbnail()) { this.getRefMetadata().getParentDataset().getParentElement().setThumbnailUrl(this.getImageUrl()); }
	}
	
	
	@Override
	public boolean isThumbnail() {

		Boolean isThumbnail = false;
		// property is inherited from parent template if any
		if (this.getRefMetadata().isTemplated()) {
			IMetadata templateRef = ((CompositeMetadata) getRefMetadata()).getTemplateReference();			 
			isThumbnail = templateRef.getAsImage().isThumbnail();
		} 
		else { isThumbnail = getRefMetadata().isValueBoolean1()==true; } 
		
		return isThumbnail;
	}
	
	@Override
	public void setThumbnail(boolean val) throws DataAccessConstraintException { 
		getRefMetadata().setValueBoolean1(val);
		if (this.isThumbnail()) { this.getRefMetadata().getParentDataset().getParentElement().setThumbnailUrl(this.getImageUrl()); }
	}
	@Override
	public void setBorderSize(Long borderSize) { getRefMetadata().setValueNumber1(new Double(borderSize)); }
	@Override
	public Long getBorderSize() { 
		// property is still inherited from parent template
		if (getRefMetadata().isTemplated()) {
			return ((CompositeMetadata) getRefMetadata()).getTemplateReference().getAsImage().getBorderSize();
		} 
		else {
			return java.lang.Math.round(getRefMetadata().getValueNumber1());
		}
	}
	@Override
	public void setBorderColor(String color) { getRefMetadata().setString1(color);}
	@Override
	public String getBorderColor() {
		if (getRefMetadata().isTemplated()) {
			return ((CompositeMetadata) getRefMetadata()).getTemplateReference().getAsImage().getBorderColor();
		} else {
			return getRefMetadata().getString1();
		}
	}
	
	@Override
	public String getSearchText() {
		return getRefMetadata().getLongString();
	}

	@Override
	public void setValueFromStr(String str) throws InapropriateStringForTypeException {
		try {
			this.setImageUrl(str);
		} catch (Exception e) { 
			throw new InapropriateStringForTypeException(e.getMessage()); 
		}		
	}
}
