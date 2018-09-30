package metaindex.data.metadata;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.community.ACommunitySubdata;
import metaindex.data.community.CommunityDatatype;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunityTerm;
import metaindex.data.community.ICommunityTermData;
import metaindex.data.dataset.IDataset;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.IMetadataFunctions.InapropriateStringForTypeException;
import metaindex.data.metadata.specialized.IMetadata_Image;
import metaindex.data.metadata.specialized.IMetadata_LongText;
import metaindex.data.metadata.specialized.IMetadata_Number;
import metaindex.data.metadata.specialized.IMetadata_TinyText;
import metaindex.data.metadata.specialized.IMetadata_WebLink;
import metaindex.data.metadata.specialized.Metadata_Image;
import metaindex.data.metadata.specialized.Metadata_LongText;
import metaindex.data.metadata.specialized.Metadata_Number;
import metaindex.data.metadata.specialized.Metadata_TinyText;
import metaindex.data.metadata.specialized.Metadata_WebLink;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.AJsonEncodable;
import metaindex.dbaccess.IDBAccessedData.BeanDataException;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Bean storing community data
 * @author laurent
 *
 */
public class Metadata extends ACommunitySubdata<IMetadata> implements IMetadata {

	/**
	 * Just used for JSON encoding
	 * Cannot use directly Metadata class (crashes, don't know really why)
	 * @author laurent
	 *
	 */
	protected class MetadataExchange implements IMetadataContents {
		
		IMetadata myMetadata;
		MetadataExchange(IMetadata m) {myMetadata=m;}
		@Override public String getName() { return myMetadata.getName(); }		
		@Override public Integer getMetadataId() { return myMetadata.getMetadataId(); }
		@Override public void setMetadataId(Integer metadataId) { myMetadata.setMetadataId(metadataId); }
		@Override public void setName(String name) { myMetadata.setName(name); }
		@Override public String getComment() { return myMetadata.getComment(); }
		@Override public void setComment(String comment) { myMetadata.setComment(comment); }
		@Override public Integer getTermId() { return myMetadata.getTermId(); }
		@Override public void setTermId(Integer termId) { myMetadata.setTermId(termId); }
		@Override public Integer getDatasetId() { return myMetadata.getDatasetId(); }
		@Override public void setDatasetId(Integer datasetId) { myMetadata.setDatasetId(datasetId); }
		@Override public Integer getLayoutColumn() { return myMetadata.getLayoutColumn(); }
		@Override public void setLayoutColumn(Integer layoutColumn) { myMetadata.setLayoutColumn(layoutColumn); }
		@Override public boolean isLayoutDoDisplayName() { return myMetadata.isLayoutDoDisplayName(); }
		@Override public void setLayoutDoDisplayName(Boolean layoutDoDisplayName) { myMetadata.setLayoutDoDisplayName(layoutDoDisplayName); }
		@Override public String getLayoutAlign() { return myMetadata.getLayoutAlign(); }
		@Override public void setLayoutAlign(String layoutAlign) { myMetadata.setLayoutAlign(layoutAlign); }
		@Override public String getLayoutSize() { return myMetadata.getLayoutSize(); }
		@Override public void setLayoutSize(String layoutSize) { myMetadata.setLayoutSize(layoutSize); }
		@Override public String getString1() { return myMetadata.getString1(); }
		@Override public void setString1(String string1) { myMetadata.setString1(string1); }
		@Override public String getString2() { return myMetadata.getString2(); }
		@Override public void setString2(String string2) { myMetadata.setString2(string2); }
		@Override public String getString3() { return myMetadata.getString3(); }
		@Override public void setString3(String string3) { myMetadata.setString3(string3); }
		@Override public String getString4() { return myMetadata.getString4(); }
		@Override public void setString4(String string4) { myMetadata.setString4(string4); }
		@Override public String getLongString() { return myMetadata.getLongString(); }
		@Override public void setLongString(String longString) { myMetadata.setLongString(longString); }
		@Override public Double getValueNumber1() { return myMetadata.getValueNumber1(); }
		@Override public void setValueNumber1(Double valueNumber1) { myMetadata.setValueNumber1(valueNumber1); }
		@Override public Double getValueNumber2() { return myMetadata.getValueNumber2(); }
		@Override public void setValueNumber2(Double valueNumber2) { myMetadata.setValueNumber2(valueNumber2); }
		@Override public Double getValueNumber3() { return myMetadata.getValueNumber3(); }
		@Override public void setValueNumber3(Double valueNumber3) { myMetadata.setValueNumber3(valueNumber3); }
		@Override public Double getValueNumber4() { return myMetadata.getValueNumber4(); }
		@Override public void setValueNumber4(Double valueNumber4) { myMetadata.setValueNumber4(valueNumber4); }
		@Override public int getLayoutPosition() { return myMetadata.getLayoutPosition(); }
		@Override public void setLayoutPosition(Integer layoutPosition) { myMetadata.setLayoutPosition(layoutPosition); }
		
		
	}
	
	
	private static boolean showWarningMsgDone = true;
	
	private Log log = LogFactory.getLog(Metadata.class);
	
	private boolean isSynchronized =false;
	
	private Integer metadataId=0;
	private Integer termId=0;
	private Integer datasetId=0;
	private String name="";	
	private String comment="";	
	private Integer layoutColumn=0;
	private Integer layoutPosition=0;
	private boolean layoutDoDisplayName=true;
	private String layoutAlign="center";
	private String layoutSize="normal";
	private String string1="";
	private String string2="";
	private String string3="";
	private String string4="";
	private String longString="";
	private Double valueNumber1=0.0;
	private Double valueNumber2=0.0;
	private Double valueNumber3=0.0;
	private Double valueNumber4=0.0;
	private boolean valueBoolean1=false;
	private boolean valueBoolean2=false;
	private boolean valueBoolean3=false;
	private boolean valueBoolean4=false;
	
	
	private IDataset parentDataset=null;
	
	public static class PositionComparator implements Comparator<Metadata> {

		@Override
		public int compare(Metadata o1, Metadata o2) {
			if (o1.getLayoutPosition()<o2.getLayoutPosition()) { return -1; }
			if (o1.getLayoutPosition()>o2.getLayoutPosition()) { return 1; }
			return 0;
		}
		
	}
	
	public Metadata(ICommunity myCommunity) {
		super(myCommunity,CommunitiesAccessor.getDataAccessors());
		
	}
	@Override
	public void dump(String depthStr) {
		log.error(depthStr+"Metadata "+this.getMetadataId()+" : "+this.getName());
		
	}
	
	@Override
	public void checkDataDBCompliance() throws DataAccessConstraintException {
		checkCompliantWithDBSmallString("field 'name' of metadata '"+this.getName()+"'",this.getName());
		checkCompliantWithDBSmallString("field 'comment' of metadata '"+this.getName()+"'",this.getComment());
		checkCompliantWithDBSmallString("field 'string1' of metadata '"+this.getName()+"'",this.getString1());
		checkCompliantWithDBSmallString("field 'string2' of metadata '"+this.getName()+"'",this.getString2());
		checkCompliantWithDBSmallString("field 'string3' of metadata '"+this.getName()+"'",this.getString3());
		checkCompliantWithDBSmallString("field 'string4' of metadata '"+this.getName()+"'",this.getString4());
		checkCompliantWithDBLongString("field 'longString' of metadata '"+this.getName()+"'",this.getLongString());		
	}
	
	@Override
	public IMetadata clone() {
		try {
			IMetadata clone = new Metadata(this.getCommunityData());
			clone.setMetadataId(metadataId);
			clone.setName(name);
			clone.setComment(comment);
			clone.setDatasetId(datasetId);
			clone.setLayoutAlign(layoutAlign);
			clone.setLayoutColumn(layoutColumn);
			clone.setLayoutDoDisplayName(layoutDoDisplayName);
			clone.setLayoutPosition(layoutPosition);
			clone.setLayoutSize(layoutSize);
			clone.setLongString(longString);
			clone.setValueNumber1(valueNumber1);
			clone.setValueNumber2(valueNumber2);
			clone.setValueNumber3(valueNumber3);
			clone.setValueNumber4(valueNumber4);
			clone.setValueBoolean1(valueBoolean1);
			clone.setValueBoolean2(valueBoolean2);
			clone.setValueBoolean3(valueBoolean3);
			clone.setValueBoolean4(valueBoolean4);
			clone.setTermId(termId);
			clone.setString1(string1);
			clone.setString2(string2);
			clone.setString3(string3);
			clone.setString4(string4);
			clone.setReadOnly(true);
			return clone;
		} catch (DataAccessConstraintException e) {
			log.error("Unable to clone object : "+e);
			return null;
		}
		
	}
	
  	@Override
  	public boolean isIdentified() {
  		return this.getMetadataId()>0 && this.getDatasetId()>0;
  	}
	
	@Override
	public void commitFull(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		// no sub data for Metadata
		this.commit(activeUser);		
	}

	@Override
	public boolean isSynchronized() {		
		return isSynchronized;
	}

	@Override
  	public boolean isImage() { 

  		return this.getCommunityData().getTermDataById(this.getTermId()).getDatatypeName()
  								.equals(this.getMetadataDBAccessor().getImageDatatypeName());
	}
	@Override
  	public boolean isNumber() { 

  		return this.getCommunityData().getTermDataById(this.getTermId()).getDatatypeName()
  								.equals(this.getMetadataDBAccessor().getNumberDatatypeName());
	}
	@Override
   	public boolean isWebLink() { 

  		return this.getCommunityData().getTermDataById(this.getTermId()).getDatatypeName()
  								.equals(this.getMetadataDBAccessor().getWebLinkDatatypeName());
	}
	@Override
  	public boolean isTinyText() { 
  		return this.getCommunityData().getTermDataById(this.getTermId()).getDatatypeName()
  								.equals(this.getMetadataDBAccessor().getTinyTextDatatypeName());
	}	
	@Override
  	public boolean isLongText() { 
  		return this.getCommunityData().getTermDataById(this.getTermId()).getDatatypeName()
  								.equals(this.getMetadataDBAccessor().getLongTextDatatypeName());
	}	
  	

	/**
	 * Load from DB corresponding data
	 * Build also the corresponding 'searchText' string used for client side javascript search.
	 * @param activeUser
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	@Override
	public void update(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException,DataReferenceErrorException {
		getMetadataDBAccessor().refreshFromDB(activeUser, this);		
	}		
	

	@Override
	public void invalidate() {
		this.isSynchronized=false;
		
	}

	@Override
	public void updateFull(IUserProfileData activeUser)
			throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
		this.update(activeUser);
		
		isSynchronized=true;
		
	}

	public List<IMetadata> loadAssociatedData(IUserProfileData activeUser, IDataset dataset) {
		return getMetadataDBAccessor().loadAssociatedData(activeUser, parentDataset);
		
	}

	
	@Override
	public void commit(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException {
			this.checkDataDBCompliance();
			getMetadataDBAccessor().storeIntoDB(activeUser, this);
			isSynchronized=false;
	}
	
	@Override
	public void create(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		this.getMetadataDBAccessor().createIntoDB(activeUser, this);
	}
	
	@Override
	public void delete(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		this.getMetadataDBAccessor().deleteFromDB(activeUser, this);
		
	}	
	
	@Override
	public ICommunityTerm getTerm() {
		if (this.getCommunityData()==null) { 
			throw new DataAccessErrorException("For Metadata '"+this.getMetadataId()
				+"' : no selected community, unable to retrieve datatypeId.");
		}
		return this.getCommunityData().getTermDataById(this.getTermId());
				
	}
	
	@Override
	public Integer getDatatypeId() throws DataAccessErrorException {
		
		ICommunityTermData term = this.getTerm();
		if (term==null) { 
			throw new DataAccessErrorException("For Metadata '"+this.getMetadataId()
														+"' : Term '"+this.getTermId()+"' not found in Community '"
														+this.getCommunityData().getIdName()+"'"); 
		}
		
		return term.getDatatypeId();						
	}
	
	
	@Override
	public String getComment() {
		return comment;
	}
	@Override
	public void setComment(String comment) throws DataAccessConstraintException {
		this.comment = comment;
	}
	@Override
	public Integer getMetadataId() {
		return metadataId;
	}
	
	public void setMetadataId(Integer metadataId) {
		this.metadataId = metadataId;
	}
	@Override
	public Integer getTermId() {
		return termId;
	}
	@Override
	public void setTermId(Integer termId) {
		this.termId = termId;
	}
	@Override
	public Integer getDatasetId() {
		return datasetId;
	}

	/**
	 * Only used by the Metadata object
	 */
	@Override
	public void setParentDataset(IDataset dataset) {
		this.parentDataset=dataset; 
		this.datasetId=dataset.getDatasetId();
	}
	
	/**
	 *  Get the Dataset to which belong this Metadata
	 * @return
	 */
	@Override
	public IDataset getParentDataset() {
		if (parentDataset==null && datasetId!=0
				|| parentDataset.getDatasetId()!=datasetId) {
			parentDataset=this.getCommunityData().getDataset(datasetId);			
		}		
		return parentDataset;
	}
	@Override
	public void setDatasetId(Integer datasetId) {		
		Integer prevDatasetId = this.datasetId;
		this.datasetId = datasetId;
		if (prevDatasetId!=0 && !datasetId.equals(prevDatasetId)) {
			IDataset newDataset = this.getCommunityData().getDataset(datasetId);
			newDataset.addMetadata(this);
			if (prevDatasetId!=0) { 
				IDataset prevDataset = this.getCommunityData().getDataset(prevDatasetId);
				prevDataset.removeMetadata(this.getDatasetId()); 				
			}
		}
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public Integer getLayoutColumn() {
		return layoutColumn;
	}
	@Override
	public void setLayoutColumn(Integer layoutColumn) {
		this.layoutColumn = layoutColumn;
	}
	@Override
	public boolean isLayoutDoDisplayName() {
		return layoutDoDisplayName;
	}
	@Override
	public void setLayoutDoDisplayName(Boolean layoutDoDisplayName) {
		this.layoutDoDisplayName = layoutDoDisplayName;
	}
	@Override
	public String getLayoutAlign() {
		return layoutAlign;
	}
	@Override
	public void setLayoutAlign(String layoutAlign) {
		this.layoutAlign = layoutAlign;
	}
	@Override
	public String getLayoutSize() {
		return layoutSize;
	}
	@Override
	public void setLayoutSize(String layoutSize) {
		this.layoutSize = layoutSize;
	}
	@Override
	public String getString1() {
		return string1;
	}
	@Override
	public void setString1(String string1) {
		this.string1 = string1;
	}
	@Override
	public String getString2() {
		return string2;
	}
	@Override
	public void setString2(String string2) {
		this.string2 = string2;
	}
	@Override
	public String getString3() {
		return string3;
	}
	@Override
	public void setString3(String string3) {
		this.string3 = string3;
	}
	@Override
	public String getString4() {
		return string4;
	}
	@Override
	public void setString4(String string4) {
		this.string4 = string4;
	}
	@Override
	public String getLongString() {		
		return longString;
	}
	@Override
	public void setLongString(String longString) {
		this.longString = longString;	
	}
	@Override
	public Double getValueNumber1() {
		return valueNumber1;
	}
	@Override
	public void setValueNumber1(Double valueNumber1) {
		this.valueNumber1 = valueNumber1;
	}
	@Override
	public Double getValueNumber2() {
		return valueNumber2;
	}
	@Override
	public void setValueNumber2(Double valueNumber2) {
		this.valueNumber2 = valueNumber2;
	}
	@Override
	public Double getValueNumber3() {
		return valueNumber3;
	}
	@Override
	public void setValueNumber3(Double valueNumber3) {
		this.valueNumber3 = valueNumber3;
	}
	@Override
	public Double getValueNumber4() {
		return valueNumber4;
	}
	@Override
	public void setValueNumber4(Double valueNumber4) {
		this.valueNumber4 = valueNumber4;
	}
	
	@Override
	public int getLayoutPosition() {
		return layoutPosition;
	}
	@Override
	public void setLayoutPosition(Integer layoutPosition) {
		this.layoutPosition = layoutPosition;
	}
	
	protected String getDataSearchText() {
		String result="";		
		if (this.isImage()) { result = this.getAsImage().getSearchText(); } 
		else {			
			if (showWarningMsgDone) {
				log.error("TODO: finish specialized Metadata types");
				showWarningMsgDone=false;
			}
			if (this.getLongString().length()>0) { result+=" "+this.getLongString(); }
			if (this.getString1().length()>0) { result+=" "+this.getString1(); }
			if (this.getString2().length()>0) { result+=" "+this.getString2(); }
			if (this.getString3().length()>0) { result+=" "+this.getString3(); }
			if (this.getString4().length()>0) { result+=" "+this.getString4(); }						
		}
		return result;
	}
	
	@Override
	public String getSearchText() {
		return this.getName()+";"+this.getComment()+";"+this.getDataSearchText()+";";
	}

	@Override
	public boolean isValueBoolean1() {
		return valueBoolean1;
	}
	@Override
	public int getValueBoolean1() {
		if (isValueBoolean1()) { return 1; }
		else { return 0; }
	}
	@Override
	public void setValueBoolean1(boolean valueBoolean1) {
		this.valueBoolean1 = valueBoolean1;		
	}

	@Override
	public boolean isValueBoolean2() {
		return valueBoolean2;
	}
	@Override
	public int getValueBoolean2() {
		if (isValueBoolean2()) { return 1; }
		else { return 0; }
	}
	@Override
	public void setValueBoolean2(boolean valueBoolean2) {
		this.valueBoolean2 = valueBoolean2;
	}

	@Override
	public boolean isValueBoolean3() {
		return valueBoolean3;
	}
	@Override
	public int getValueBoolean3() {
		if (isValueBoolean3()) { return 1; }
		else { return 0; }
	}
	@Override
	public void setValueBoolean3(boolean valueBoolean3) {
		this.valueBoolean3 = valueBoolean3;
	}

	@Override
	public boolean isValueBoolean4() {
		return valueBoolean4;
	}
	@Override
	public int getValueBoolean4() {
		if (isValueBoolean4()) { return 1; }
		else { return 0; }
	}
	@Override
	public void setValueBoolean4(boolean valueBoolean4) {
		this.valueBoolean4 = valueBoolean4;
	}

	@Override
	public IMetadata_Image getAsImage() { return new Metadata_Image(this); }

	@Override
	public IMetadata_Number getAsNumber() { return new Metadata_Number(this); }

	@Override
	public IMetadata_WebLink getAsWebLink() { return new Metadata_WebLink(this); }

	@Override
	public IMetadata_TinyText getAsTinyText() { return new Metadata_TinyText(this); }

	@Override
	public IMetadata_LongText getAsLongText() { return new Metadata_LongText(this); }


	@Override
	public void setValueFromStr(String str) throws InapropriateStringForTypeException {
		try {
			if (this.isImage()) { this.getAsImage().setValueFromStr(str); }
			else if (this.isNumber()) { this.getAsNumber().setValueFromStr(str); }
			else if (this.isWebLink()) { this.getAsWebLink().setValueFromStr(str); }
			else if (this.isTinyText()) { this.getAsTinyText().setValueFromStr(str); }
			else if (this.isLongText()) { this.getAsLongText().setValueFromStr(str); }
			else { 
				throw  new InapropriateStringForTypeException("Unhandled datatype "+this.getTerm().getDatatypeName()+" for setValueFromStr"); 
			}
			
		} catch (DataAccessConstraintException e) { throw new InapropriateStringForTypeException(e.getMessage()); }
	}
	
	@Override
	public JSONObject encode() {
		
		try {
			return new JSONObject(new Metadata.MetadataExchange(this));			 
		} catch (Exception e) {
			log.error("Error Json : "+e.getMessage());
		}
		return null;
	}
	
	
	
	
	

}
