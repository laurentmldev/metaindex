package metaindex.data.community;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IGenericEncodable.UnableToPopulateException;
import metaindex.dbaccess.accessors.AMetaindexAccessor;

/**
 * Store data of a community term
 * @author laurent
 *
 */
public class CommunityTerm extends  AMultiLanguageCommunitySubdata<ICommunityTermData,TermVocabularySet> implements ICommunityTerm {
	
	private Log log = LogFactory.getLog(CommunityTerm.class);
	private boolean isSynchronized=false;
	private int termId;
	private int datatypeId;
	//private String datatypeName="";
	private boolean isEnum;
	
	public CommunityTerm(ICommunity parentCommunityData) throws DataAccessErrorException {
		super(parentCommunityData,ADataAccessFactory.getDataAccessImplFactory(
		ADataAccessFactory.DATA_ACCESS_IMPL_DB_METAINDEX));	
		this.setCommunityId(parentCommunityData.getCommunityId());
	}
	
  	@Override
  	public boolean isIdentified() {
  		return this.getTermId()>0 && this.getCommunityId()>0;
  	}	
  	@Override
	public void checkDataDBCompliance() throws DataAccessConstraintException {
		// nothing to check		
	}
  	
	@Override
	public int getTermId() {
		return termId;
	}
	@Override
	public void setTermId(int termId) {
		this.termId = termId;
	}
	@Override
	public int getDatatypeId() {
		return datatypeId;
	}
	@Override
	public void setDatatypeId(int dataTypeId) {
		this.datatypeId = dataTypeId;
	}
	@Override
	public boolean isEnum() {
		return isEnum;
	}
	@Override
	public void setEnum(boolean isEnum) {
		this.isEnum = isEnum;
	}

	@Override
	public JSONObject encode() { 
		JSONObject jsonterm = new JSONObject();
		jsonterm.put("idName", this.getIdName());
		jsonterm.put("datatypeId", this.getDatatypeId());		
		return jsonterm;
	}
	
	@Override
	public void update(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException {	
		this.getCommunityTermsDBAccessor().refreshFromDB(activeUser, this);
		isSynchronized=true;
	}

	@Override
	public void create(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		this.getCommunityTermsDBAccessor().createIntoDB(activeUser, this);	
	}

	@Override
	public void commit(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		this.getCommunityTermsDBAccessor().storeIntoDB(activeUser, this);
		
	}

	@Override
	public void delete(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		this.getCommunityTermsDBAccessor().deleteFromDB(activeUser, this);
		
	}

	@Override
	public void invalidate() {
		isSynchronized=false;		
	}

	@Override
	public void commitFull(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		commit(activeUser);	
	}

	@Override
	public void updateFull(IUserProfileData activeUser)
			throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
		update(activeUser);		
	}

	@Override
	public boolean isSynchronized() {
		return isSynchronized;
	}
		
	public String getDatatypeName() {
		return this.getCommunityData().getDatatypeName(this.getDatatypeId());
	}


	@Override
	public TermVocabularySet getVocabulary(int guiLanguageId) {
		Iterator<TermVocabularySet> it = getVocabularySets().iterator();
		while (it.hasNext()) {
			TermVocabularySet cur=it.next();
			if (cur.getGuiLanguageID()==guiLanguageId) { return cur; }
		}
		return null;
		
	}

	@Override
	public boolean isModifyOverridenTemplate() {
		return false;
	}

	@Override
	public boolean isTemplated() {
		return false;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public void setReadOnly(boolean isReadOnly) {
		// do nothing (always read-only
		log.warn("CommunityTermData is always read-only, cannot set it to "+isReadOnly);
	}

	@Override
	public ICommunityTermData clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	@Override
	public void populateFromJson(IUserProfileData activeUser, JSONObject json, POPULATE_POLICY policy) throws UnableToPopulateException {
		this.populateFromJson(null, json, policy);
		this.commit(activeUser);
		
	}

	@Override
	public TermVocabularySet getVocabulary(IUserProfileData activeUser) {
		return this.getVocabulary(activeUser.getGuiLanguageId());
	}

}
