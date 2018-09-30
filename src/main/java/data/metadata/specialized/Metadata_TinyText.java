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
public class Metadata_TinyText extends AMetadata_Specialized implements IMetadata_TinyText {
	
	private Log log = LogFactory.getLog(Metadata_TinyText.class);
	private static String FONTWEIGHT_NORMAL="Normal";
	private static String FONTWEIGHT_BOLD="Bold";
	private static String FONTWEIGHT_ITALIC="Italic";
	
	public Metadata_TinyText(IMetadata metadata) {
		super(metadata);		
	}
	
	@Override
	public String getText() { return getRefMetadata().getString1(); }
	@Override
	public void setText(String tinyText) throws DataAccessConstraintException { 
		getRefMetadata().setString1(tinyText); 
	}
	@Override
	public String getSearchText() {
		return getRefMetadata().getString1();
	}
	
	/**
	 * We don't know if this field has been used by another type before being a TinyText' font weight.
	 * So we use a default value if it doesn't match any of the predefined ones.
	 * @return
	 */
	@Override
	public String getFontWeight() { 				
		if (!getRefMetadata().getString2().equals(FONTWEIGHT_BOLD) && !getRefMetadata().getString2().equals(FONTWEIGHT_ITALIC)) { return FONTWEIGHT_NORMAL; }
		else { return getRefMetadata().getString2(); }
	}
	@Override
	public void setFontWeight(String fontWeight) { 
		getRefMetadata().setString2(fontWeight); 
	}
	
	@Override
	public void setValueFromStr(String str) throws InapropriateStringForTypeException {
		try {
			this.setText(str);
		} catch (Exception e) { throw new InapropriateStringForTypeException(e.getMessage()); }		
	}
	
}
