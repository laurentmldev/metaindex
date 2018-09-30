package metaindex.data.metadata;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.community.ACommunitySubdata;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityTerm;
import metaindex.data.community.ICommunity;
import metaindex.data.dataset.Dataset;
import metaindex.data.dataset.IDataset;
import metaindex.data.element.ICompositeData;
import metaindex.data.metadata.specialized.Metadata_Image;
import metaindex.data.metadata.specialized.Metadata_LongText;
import metaindex.data.metadata.specialized.Metadata_TinyText;
import metaindex.data.metadata.specialized.Metadata_WebLink;
import metaindex.data.userprofile.IUserProfileData;
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
public class CompositeMetadata extends Metadata implements ICompositeData<IMetadata>{


	
	private Log log = LogFactory.getLog(CompositeMetadata.class);
	
	private IDataset parentDataset;
	private IMetadata templateReference;  
	private IMetadata templateInstance;  
	
	/**
	 * Metadata made of a Template reference data and an instance data which might modify value of its template.
	 * Instance can be null, then template metadata values are returned.
	 * Layout data is from template exclusively.
	 * @param myCommunity
	 * @param templateReference
	 * @param templateInstance can be null
	 */
	public CompositeMetadata(ICommunity myCommunity, IMetadata templateReference, IMetadata templateInstance) {
		super(myCommunity);
		this.setTemplateReference(templateReference);
		this.setTemplateInstance(templateInstance);
	}
	
	public CompositeMetadata(ICommunity myCommunity, IMetadata templateReference, IDataset parentDataset) {
		super(myCommunity);
		this.setTemplateReference(templateReference);
		this.parentDataset=parentDataset;
	}
	
	
	@Override
	public IMetadata clone() {
		IMetadata clone = new CompositeMetadata(this.getCommunityData(),this.getTemplateReference(),this.getTemplateInstance());		
					
		clone.setReadOnly(true);
		return clone;
	}
	
	@Override
	public boolean isModifyOverridenTemplate() {
		return this.getTemplateInstance()!=null;
	}
	@Override 
	public boolean isTemplated() {
		return true;
	}
	@Override
	public Metadata_Image getAsImage() { return new Metadata_Image(this); }

	@Override
	public Metadata_WebLink getAsWebLink() { return new Metadata_WebLink(this); }

	@Override
	public Metadata_TinyText getAsTinyText() { return new Metadata_TinyText(this); }

	@Override
	public Metadata_LongText getAsLongText() { return new Metadata_LongText(this); }


	@Override
	public void update(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException  {
		
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().update(activeUser); }
		this.getTemplateReference().update(activeUser);						 
	}	
	@Override
	public void updateFull(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException  {
		
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().updateFull(activeUser); }
		this.getTemplateReference().updateFull(activeUser);						 
	}		
	@Override
	public void commit(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().commit(activeUser); }
	}
	@Override
	public void commitFull(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().commitFull(activeUser); }
	}	
	@Override
	public void create(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().create(activeUser); }
	}
	
	@Override
	public void delete(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().delete(activeUser); }
		
	}	
	
	public String getComment() {
		if (this.getTemplateInstance()!=null) { return this.getTemplateInstance().getComment(); }
		else { return this.getTemplateReference().getComment(); }
	}

	public void setComment(String comment) {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().setComment(comment); }
	}

	public Integer getMetadataId() {
		if (this.getTemplateInstance()!=null) { return this.getTemplateInstance().getMetadataId(); }
		else { return this.getTemplateReference().getMetadataId(); }
	}

	public void setMetadataId(Integer metadataId) {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().setMetadataId(metadataId); } 
		else {
			// disabled for RO composite metadata
			log.error("Warning : trying to setMetadataId of a RO composite metadata. Ignored.");
		}
	}

	public Integer getTermId() {
		return this.getTemplateReference().getTermId();
	}

	public void setTermId(Integer termId) {
		// disabled for composite metadata
		log.error("Warning : trying to setTermId of a composite metadata. Ignored.");
	}

	public Integer getDatasetId() {
		if (this.getTemplateInstance()!=null) { return this.getTemplateInstance().getDatasetId(); }
		else { return this.parentDataset.getDatasetId(); }
	}

	/**
	 * Only used by the Metadata object
	 */
	public void setParentDataset(Dataset dataset) {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().setParentDataset(dataset); }
		else { 
			// disabled for RO composite metadata
			log.error("Warning : trying to setParentDataset of a RO composite metadata. Ignored.");
		}
	}
	
	/**
	 *  Get the Dataset to which belong this Metadata
	 * @return
	 */
	@Override
	public IDataset getParentDataset() {
		if (this.getTemplateInstance()!=null) { return this.getTemplateInstance().getParentDataset(); }
		else { return this.getTemplateReference().getParentDataset(); }
	}
	
	public void setDatasetId(Integer datasetId) {
		// disabled for composite metadata
		log.error("Warning : trying to setDatasetId of a composite metadata. Ignored.");
	}

	public String getName() {
		if (this.getTemplateInstance()!=null) { return this.getTemplateInstance().getName(); }
		else { return this.getTemplateReference().getName(); }
	}

	public void setName(String name) {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().setName(name); }
		else {
			// disabled for RO composite metadata
			log.error("Warning : trying to setName of a RO composite metadata. Ignored.");
		}
	}

	public Integer getLayoutColumn() {
		return this.getTemplateReference().getLayoutColumn();
	}

	public void setLayoutColumn(Integer layoutColumn) {
		// disabled for composite metadata
		log.error("Warning : trying to setLayoutColumn of a composite metadata. Ignored.");
	}

	public boolean isLayoutDoDisplayName() {
		return this.getTemplateReference().isLayoutDoDisplayName();
	}

	public void setLayoutDoDisplayName(Boolean layoutDoDisplayName) {
		// disabled for composite metadata
		log.error("Warning : trying to setLayoutDoDisplayName of a composite metadata. Ignored.");
	}

	public String getLayoutAlign() {
		return this.getTemplateReference().getLayoutAlign();
	}

	public void setLayoutAlign(String layoutAlign) {
		// disabled for composite metadata
		log.error("Warning : trying to setLayoutAlign of a composite metadata. Ignored.");
	}

	public String getLayoutSize() {
		return this.getTemplateReference().getLayoutSize();
	}

	public void setLayoutSize(String layoutSize) {
		// disabled for composite metadata
		log.error("Warning : trying to setLayoutSize of a composite metadata. Ignored.");
	}

	public String getString1() {
		if (this.getTemplateInstance()!=null) { return this.getTemplateInstance().getString1(); }
		else { return this.getTemplateReference().getString1(); }
	}

	public void setString1(String string1) {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().setString1(string1); }
		else { 
			// disabled for composite metadata
			log.error("Warning : trying to setString1 of a RO composite metadata. Ignored.");
		}
	}

	public String getString2() {
		if (this.getTemplateInstance()!=null) { return this.getTemplateInstance().getString2(); }
		else { return this.getTemplateReference().getString2(); }
	}

	public void setString2(String string2) {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().setString2(string2); }
		else { 
			// disabled for composite metadata
			log.error("Warning : trying to setString2 of a RO composite metadata. Ignored.");
		}
	}

	public String getString3() {
		if (this.getTemplateInstance()!=null) { return this.getTemplateInstance().getString3(); }
		else { return this.getTemplateReference().getString3(); }
	}

	public void setString3(String string3) {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().setString3(string3); }
		else { 
			// disabled for composite metadata
			log.error("Warning : trying to setString3 of a RO composite metadata. Ignored.");
		}
	}


	public String getString4() {
		if (this.getTemplateInstance()!=null) { return this.getTemplateInstance().getString4(); }
		else { return this.getTemplateReference().getString4(); }
	}

	public void setString4(String string4) {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().setString4(string4); }
		else { 
			// disabled for composite metadata
			log.error("Warning : trying to setString4 of a RO composite metadata. Ignored.");
		}
	}

	public String getLongString() {
		if (this.getTemplateInstance()!=null) {
			return this.getTemplateInstance().getLongString(); 
		}
		else {
			return this.getTemplateReference().getLongString(); 
		}
	}

	public void setLongString(String longString) {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().setLongString(longString); }
		else { 
			// disabled for composite metadata
			log.error("Warning : trying to setLongString of a RO composite metadata. Ignored.");
		}
	}

	public Double getValueNumber1() {
		if (this.getTemplateInstance()!=null) { return this.getTemplateInstance().getValueNumber1(); }
		else { return this.getTemplateReference().getValueNumber1(); }
	}

	public void setValueNumber1(Double valueNumber1) {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().setValueNumber1(valueNumber1); }
		else { 
			// disabled for composite metadata
			log.error("Warning : trying to setValueNumber1 of a RO composite metadata. Ignored.");
		}
	}

	public Double getValueNumber2() {
		if (this.getTemplateInstance()!=null) { return this.getTemplateInstance().getValueNumber2(); }
		else { return this.getTemplateReference().getValueNumber2(); }
	}

	public void setValueNumber2(Double valueNumber2) {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().setValueNumber2(valueNumber2); }
		else { 
			// disabled for composite metadata
			log.error("Warning : trying to setValueNumber2 of a RO composite metadata. Ignored.");
		}
	}
	public Double getValueNumber3() {
		if (this.getTemplateInstance()!=null) { return this.getTemplateInstance().getValueNumber3(); }
		else { return this.getTemplateReference().getValueNumber3(); }
	}

	public void setValueNumber3(Double valueNumber3) {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().setValueNumber3(valueNumber3); }
		else { 
			// disabled for composite metadata
			log.error("Warning : trying to setValueNumber3 of a RO composite metadata. Ignored.");
		}
	}
	public Double getValueNumber4() {
		if (this.getTemplateInstance()!=null) { return this.getTemplateInstance().getValueNumber4(); }
		else { return this.getTemplateReference().getValueNumber4(); }
	}

	public void setValueNumber4(Double valueNumber4) {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().setValueNumber4(valueNumber4); }
		else { 
			// disabled for composite metadata
			log.error("Warning : trying to setValueNumber4 of a RO composite metadata. Ignored.");
		}
	}
	/**
	 * @return the layoutPosition
	 */
	public int getLayoutPosition() {
		return this.getTemplateReference().getLayoutPosition();
	}

	/**
	 * @param layoutPosition the layoutPosition to set
	 */
	public void setLayoutPosition(Integer layoutPosition) {
		// disabled for composite metadata
		log.error("Warning : trying to setLayoutPosition of a composite metadata. Ignored.");
	}


	/**
	 * @return the valueBoolean1
	 */
	public boolean isValueBoolean1() {
		if (this.getTemplateInstance()!=null) { return this.getTemplateInstance().isValueBoolean1(); }
		else { return this.getTemplateReference().isValueBoolean1(); }
	}
	/**
	 * @param valueBoolean1 the valueBoolean1 to set
	 */
	public void setValueBoolean1(boolean valueBoolean1) {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().setValueBoolean1(valueBoolean1); }
		else { 
			// disabled for composite metadata
			log.error("Warning : trying to setValueBoolean1 of a RO composite metadata. Ignored.");
		}
	}
	
	public boolean isValueBoolean3() {
		if (this.getTemplateInstance()!=null) { return this.getTemplateInstance().isValueBoolean3(); }
		else { return this.getTemplateReference().isValueBoolean3(); }
	}
	
	public void setValueBoolean3(boolean valueBoolean3) {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().setValueBoolean3(valueBoolean3); }
		else { 
			// disabled for composite metadata
			log.error("Warning : trying to setValueBoolean3 of a RO composite metadata. Ignored.");
		}
	}

	public boolean isValueBoolean4() {
		if (this.getTemplateInstance()!=null) { return this.getTemplateInstance().isValueBoolean4(); }
		else { return this.getTemplateReference().isValueBoolean4(); }
	}
	
	public void setValueBoolean4(boolean valueBoolean4) {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().setValueBoolean4(valueBoolean4); }
		else { 
			// disabled for composite metadata
			log.error("Warning : trying to setValueBoolean4 of a RO composite metadata. Ignored.");
		}
	}
	/**
	 * @return the templateReference
	 */
	@Override
	public IMetadata getTemplateReference() {
		return templateReference;
	}

	/**
	 * @param templateReference the templateReference to set
	 */
	@Override
	public void setTemplateReference(IMetadata templateReference) {
		this.templateReference = templateReference;
	}

	/**
	 * @return the templateInstance
	 */
	@Override
	public IMetadata getTemplateInstance() {
		return templateInstance;
	}

	/**
	 * @param templateInstance the templateInstance to set
	 */
	@Override
	public void setTemplateInstance(IMetadata templateInstance) {
		this.parentDataset=null;
		this.templateInstance = templateInstance;
	}

	

}
