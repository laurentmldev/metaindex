package metaindex.data.metadata;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.PlatformTransactionManager;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.community.ACommunitySubdata;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityTerm;
import metaindex.data.community.CommunityTermHandle;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunitySubdata;
import metaindex.data.community.ICommunityTermData;
import metaindex.data.community.ICommunityTermHandle;
import metaindex.data.dataset.DatasetHandle;
import metaindex.data.dataset.IDataset;
import metaindex.data.dataset.IDatasetHandle;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.IMetadataFunctions.InapropriateStringForTypeException;
import metaindex.data.metadata.specialized.IMetadata_Image;
import metaindex.data.metadata.specialized.IMetadata_LongText;
import metaindex.data.metadata.specialized.IMetadata_Number;
import metaindex.data.metadata.specialized.IMetadata_TinyText;
import metaindex.data.metadata.specialized.IMetadata_WebLink;
import metaindex.data.metadata.specialized.MetadataHandle_Image;
import metaindex.data.metadata.specialized.MetadataHandle_LongText;
import metaindex.data.metadata.specialized.MetadataHandle_Number;
import metaindex.data.metadata.specialized.MetadataHandle_TinyText;
import metaindex.data.metadata.specialized.MetadataHandle_WebLink;
import metaindex.data.metadata.specialized.Metadata_Image;
import metaindex.data.metadata.specialized.Metadata_LongText;
import metaindex.data.metadata.specialized.Metadata_TinyText;
import metaindex.data.metadata.specialized.Metadata_WebLink;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;
import metaindex.dbaccess.accessors.ACommunityAccessor;
import metaindex.dbaccess.accessors.ACommunityTermsAccessor;
import metaindex.dbaccess.accessors.ADatasetAccessor;
import metaindex.dbaccess.accessors.AElementAccessor;
import metaindex.dbaccess.accessors.AMetadataAccessor;
import metaindex.dbaccess.accessors.AMetaindexAccessor;
import metaindex.data.AAccessControledData;

/**
 * Bean storing community data
 * @author laurent
 *
 */
public class MetadataHandle extends AAccessControledData<IMetadata> implements IMetadataHandle {

	
	public MetadataHandle(IUserProfileData userProfile, IMetadata refData) {
		super(userProfile, refData);
	}


	@Override
	public void setValueFromStr(String str)  throws InapropriateStringForTypeException {
		this.checkWritableByUser();
		this.getRefData().setValueFromStr(str);
	}
	@Override
	public boolean isNumber() {
		this.checkReadableByUser();
		return this.getRefData().isNumber();
	}
	@Override
	public boolean isImage() {
		this.checkReadableByUser();
		return this.getRefData().isImage();
	}

	@Override
	public boolean isWebLink() {
		this.checkReadableByUser();
		return this.getRefData().isWebLink();
	}

	@Override
	public boolean isTinyText() {
		this.checkReadableByUser();
		return this.getRefData().isTinyText();
	}

	@Override
	public boolean isLongText() {
		this.checkReadableByUser();
		return this.getRefData().isLongText();
	}

	
	/**
  	 * Get an 'Image' view over this object.
  	 * The Image object point to its metadata.
  	 * @return a Metadata_Image object pointing to this metadata object. 
  	 */  	
	@Override
	public IMetadata_Image getAsImage() {
		return new MetadataHandle_Image(this.getRefData().getAsImage());
	}
	/**
  	 * Get an 'WebLink' view over this object.
  	 * The WebLink object point to its metadata.
  	 * @return a Metadata_WebLink object pointing to this metadata object. 
  	 */  	
	@Override
	public IMetadata_WebLink getAsWebLink() {
		return new MetadataHandle_WebLink(this.getRefData().getAsWebLink());
	}
	/**
  	 * Get a 'Number' view over this object.
  	 * The WebLink object point to its metadata.
  	 * @return a Metadata_WebLink object pointing to this metadata object. 
  	 */  	
	@Override
	public IMetadata_Number getAsNumber() {
		return new MetadataHandle_Number(this.getRefData().getAsNumber());
	}
	/**
  	 * Get an 'TinyText' view over this object.
  	 * The TinyText object point to its metadata.
  	 * @return a Metadata_TinyText object pointing to this metadata object. 
  	 */  	
	@Override
	public IMetadata_TinyText getAsTinyText() {
		return new MetadataHandle_TinyText(this.getRefData().getAsTinyText());
	}
	/**
  	 * Get an 'LongText' view over this object.
  	 * The LongText object point to its metadata.
  	 * @return a Metadata_LongText object pointing to this metadata object. 
  	 */
	@Override
	public IMetadata_LongText getAsLongText() {
		return new MetadataHandle_LongText(this.getRefData().getAsLongText());
	}

	@Override
	public IDatasetHandle getParentDataset() {
		IDataset parent = this.getRefData().getParentDataset();
		if (parent==null){ return null; }
		
		return new DatasetHandle(this.getUserProfile(), parent);
	}

	@Override
	public ICommunityTermHandle getTerm() {
		this.checkReadableByUser();
		return new CommunityTermHandle(this.getUserProfile(),this.getRefData().getTerm());
	}

	@Override
	public Integer getDatatypeId() throws DataAccessErrorException {
		this.checkReadableByUser();
		return this.getRefData().getDatatypeId();
	}

	@Override
	public String getComment() {
		this.checkReadableByUser();
		return this.getRefData().getComment();
	}

	@Override
	public void setComment(String comment) throws DataAccessConstraintException {
		this.checkWritableByUser();
		this.getRefData().setComment(comment);
	}

	@Override
	public Integer getMetadataId() {
		this.checkReadableByUser();
		return this.getRefData().getMetadataId();
	}

	@Override
	public Integer getTermId() {
		this.checkReadableByUser();
		return this.getRefData().getTermId();
	}

	@Override
	public void setTermId(Integer termId) {
		this.checkWritableByUser();
		this.getRefData().setTermId(termId);
	}

	@Override
	public Integer getDatasetId() {
		this.checkReadableByUser();
		return this.getRefData().getDatasetId();
	}

	@Override
	public void setDatasetId(Integer datasetId) {
		this.checkWritableByUser();
		this.getRefData().setDatasetId(datasetId);
	}
	@Override
	public String getName() {
		this.checkReadableByUser();
		return this.getRefData().getName();
	}

	@Override
	public void setName(String name) throws DataAccessConstraintException {
		this.checkWritableByUser();
		this.getRefData().setName(name);		
	}

	@Override
	public Integer getLayoutColumn() {
		this.checkReadableByUser();
		return this.getRefData().getLayoutColumn();
	}

	@Override
	public void setLayoutColumn(Integer layoutColumn) {
		this.checkWritableByUser();
		this.getRefData().setLayoutColumn(layoutColumn);
	}

	@Override
	public boolean isLayoutDoDisplayName() {
		this.checkReadableByUser();
		return this.getRefData().isLayoutDoDisplayName();
	}

	@Override
	public void setLayoutDoDisplayName(Boolean layoutDoDisplayName) {
		this.checkReadableByUser();
		this.getRefData().setLayoutDoDisplayName(layoutDoDisplayName);
	}

	@Override
	public String getLayoutAlign() {
		this.checkReadableByUser();
		return this.getRefData().getLayoutAlign();
	}

	@Override
	public void setLayoutAlign(String layoutAlign) {
		this.checkWritableByUser();
		this.getRefData().setLayoutAlign(layoutAlign);
	}

	@Override
	public String getLayoutSize() {
		this.checkReadableByUser();
		return this.getRefData().getLayoutSize();
	}

	@Override
	public void setLayoutSize(String layoutSize) {
		this.checkWritableByUser();
		this.getRefData().setLayoutSize(layoutSize);
	}

	@Override
	public String getString1() {
		this.checkReadableByUser();
		return this.getRefData().getString1();
	}

	@Override
	public void setString1(String string1) throws DataAccessConstraintException {
		this.checkWritableByUser();
		this.getRefData().setString1(string1);
	}

	@Override
	public String getString2() {
		this.checkReadableByUser();
		return this.getRefData().getString2();
	}

	@Override
	public void setString2(String string2) throws DataAccessConstraintException {
		this.checkWritableByUser();
		this.getRefData().setString2(string2);
	}

	@Override
	public String getString3() {
		this.checkReadableByUser();
		return this.getRefData().getString3();
	}

	@Override
	public void setString3(String string3) throws DataAccessConstraintException {
		this.checkWritableByUser();
		this.getRefData().setString3(string3);
	}

	@Override
	public String getString4() {
		this.checkReadableByUser();
		return this.getRefData().getString4();
	}

	@Override
	public void setString4(String string4) throws DataAccessConstraintException {
		this.checkWritableByUser();
		this.getRefData().setString4(string4);
	}

	@Override
	public String getLongString() {
		this.checkReadableByUser();
		return this.getRefData().getLongString();
	}

	@Override
	public void setLongString(String longString) throws DataAccessConstraintException {
		this.checkWritableByUser();
		this.getRefData().setLongString(longString);
	}

	@Override
	public Double getValueNumber1() {
		this.checkReadableByUser();
		return this.getRefData().getValueNumber1();
	}

	@Override
	public void setValueNumber1(Double valueNumber1) {
		this.checkWritableByUser();
		this.getRefData().setValueNumber1(valueNumber1);
	}

	@Override
	public Double getValueNumber2() {
		this.checkReadableByUser();
		return this.getRefData().getValueNumber2();
	}

	@Override
	public void setValueNumber2(Double valueNumber2) {
		this.checkWritableByUser();
		this.getRefData().setValueNumber2(valueNumber2);
	}

	@Override
	public Double getValueNumber3() {
		this.checkReadableByUser();
		return this.getRefData().getValueNumber3();
	}

	@Override
	public void setValueNumber3(Double valueNumber3) {
		this.checkWritableByUser();
		this.getRefData().setValueNumber3(valueNumber3);
	}

	@Override
	public Double getValueNumber4() {
		this.checkReadableByUser();
		return this.getRefData().getValueNumber4();
	}

	@Override
	public void setValueNumber4(Double valueNumber4) {
		this.checkWritableByUser();
		this.getRefData().setValueNumber4(valueNumber4);
	}

	@Override
	public int getLayoutPosition() {
		this.checkReadableByUser();
		return this.getRefData().getLayoutPosition();
	}

	@Override
	public void setLayoutPosition(Integer layoutPosition) {
		this.checkWritableByUser();
		this.getRefData().setLayoutPosition(layoutPosition);
	}

	@Override
	public String getSearchText() {
		this.checkReadableByUser();
		return this.getRefData().getSearchText();
	}

	@Override
	public boolean isValueBoolean1() {
		this.checkReadableByUser();
		return this.getRefData().isValueBoolean1();
	}

	@Override
	public void setValueBoolean1(boolean valueBoolean1) {
		this.checkWritableByUser();
		this.getRefData().setValueBoolean1(valueBoolean1);
	}

	@Override
	public boolean isValueBoolean2() {
		this.checkReadableByUser();
		return this.getRefData().isValueBoolean2();
	}


	@Override
	public void setValueBoolean2(boolean valueBoolean2) {
		this.checkWritableByUser();
		this.getRefData().setValueBoolean2(valueBoolean2);
	}

	@Override
	public boolean isValueBoolean3() {
		this.checkReadableByUser();
		return this.getRefData().isValueBoolean3();
	}

	

	@Override
	public void setValueBoolean3(boolean valueBoolean3) {
		this.checkWritableByUser();
		this.getRefData().setValueBoolean3(valueBoolean3);
	}

	@Override
	public boolean isValueBoolean4() {
		this.checkReadableByUser();
		return this.getRefData().isValueBoolean4();
	}


	@Override
	public void setValueBoolean4(boolean valueBoolean4) {
		this.checkWritableByUser();
		this.getRefData().setValueBoolean4(valueBoolean4);
	}

	@Override
	public Integer getCommunityId() {
		this.checkReadableByUser();
		return this.getRefData().getCommunityId();
	}

	@Override
	public boolean isReadOnly() {
		return this.getRefData().isReadOnly() || !this.isWritableByUser() 
					|| this.isTemplated() && !this.isModifyOverridenTemplate() ;
	}

	@Override
	public boolean isTemplated() {
		return this.getRefData().isTemplated();
	}

	@Override
	public boolean isModifyOverridenTemplate() {
		this.checkReadableByUser();
		return this.getRefData().isModifyOverridenTemplate();
	}


	@Override
	public void setParentDataset(IDatasetHandle d) throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException{
		
		IDataset dataset;
		dataset = CommunitiesAccessor.getCommunity(
							d.getCommunityId()).getElement(d.getParentElementData().getElementId()).getDataset(d.getDatasetId());
		this.getRefData().setParentDataset(dataset);	
			
	}

	@Override
	public void setMetadataId(Integer metadataId) {
		this.checkWritableByUser();
		this.getRefData().setMetadataId(metadataId);
		
	}

	/* Clone
	@Override
	public IMetadata clone() throws CloneNotSupportedException {
		return new UserMetadata(this.getUserProfile(), this.getRefData().clone());
	}*/



	@Override
	public int getValueBoolean1() {
		return this.getRefData().getValueBoolean1();
	}

	@Override
	public int getValueBoolean2() {
		return this.getRefData().getValueBoolean2();
	}

	@Override
	public int getValueBoolean3() {
		return this.getRefData().getValueBoolean3();
	}

	@Override
	public int getValueBoolean4() {
		return this.getRefData().getValueBoolean4();
	}


	@Override
	public void dump(String depthStr) {
		this.getRefData().dump(depthStr);		
	}	

	@Override
	public void checkDataDBCompliance() throws DataAccessConstraintException {
		this.getRefData().checkDataDBCompliance();		
	}


	
}
