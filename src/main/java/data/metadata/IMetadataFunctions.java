package metaindex.data.metadata;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.IBufferizedData;
import metaindex.data.community.ACommunitySubdata;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityTerm;
import metaindex.data.community.ICommunitySubdata;
import metaindex.data.community.ICommunityTermData;
import metaindex.data.metadata.specialized.IMetadata_Image;
import metaindex.data.metadata.specialized.IMetadata_LongText;
import metaindex.data.metadata.specialized.IMetadata_Number;
import metaindex.data.metadata.specialized.IMetadata_TinyText;
import metaindex.data.metadata.specialized.IMetadata_WebLink;
import metaindex.data.metadata.specialized.MetadataHandle_Image;
import metaindex.data.metadata.specialized.MetadataHandle_LongText;
import metaindex.data.metadata.specialized.MetadataHandle_TinyText;
import metaindex.data.metadata.specialized.MetadataHandle_WebLink;
import metaindex.data.metadata.specialized.Metadata_Image;
import metaindex.data.metadata.specialized.Metadata_LongText;
import metaindex.data.metadata.specialized.Metadata_TinyText;
import metaindex.data.metadata.specialized.Metadata_WebLink;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.accessors.AMetadataAccessor;
import metaindex.dbaccess.accessors.AMetaindexAccessor;

/**
 * Bean storing Metadata characteristic information
 * @author laurent
 *
 */
public interface IMetadataFunctions{
  	
	public class InapropriateStringForTypeException extends Exception {
		public InapropriateStringForTypeException(String msg) {
			super(msg);
		}
	};
	
	public boolean isImage();
	public boolean isNumber();
  	public boolean isWebLink();
  	public boolean isTinyText();
  	public boolean isLongText();
  	public IMetadata_Image getAsImage();
  	public IMetadata_Number getAsNumber();
  	public IMetadata_WebLink getAsWebLink();
  	public IMetadata_TinyText getAsTinyText();
  	public IMetadata_LongText getAsLongText();
  	public void setValueFromStr(String str) throws InapropriateStringForTypeException;
  	
  	/**
	 * Dump contents as log info
	 * @param depthStr string to put as prefix
	 */
	public void dump(String depthStr);
	
	public int getValueBoolean1();
	public int getValueBoolean2();
	public int getValueBoolean3();
	public int getValueBoolean4();

	/**
	 * @return the valueBoolean1
	 */
	public boolean isValueBoolean1();

	/**
	 * @param valueBoolean1 the valueBoolean1 to set
	 */
	public void setValueBoolean1(boolean valueBoolean1);

	/**
	 * @return the valueBoolean2
	 */
	public boolean isValueBoolean2();

	/**
	 * @param valueBoolean2 the valueBoolean2 to set
	 */
	public void setValueBoolean2(boolean valueBoolean2);

	/**
	 * @return the valueBoolean3
	 */
	public boolean isValueBoolean3();

	/**
	 * @param valueBoolean3 the valueBoolean3 to set
	 */
	public void setValueBoolean3(boolean valueBoolean3);

	/**
	 * @return the valueBoolean4
	 */
	public boolean isValueBoolean4();

	/**
	 * @param valueBoolean4 the valueBoolean4 to set
	 */
	public void setValueBoolean4(boolean valueBoolean4);

	public String getSearchText();
	
	public Integer getDatatypeId() throws DataAccessErrorException;
	

	

}
