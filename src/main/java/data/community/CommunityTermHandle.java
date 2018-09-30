package metaindex.data.community;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.AAccessControledData;
import metaindex.data.IBufferizedData;
import metaindex.data.element.ElementHandle;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;

/**
 * Bean storing community data
 * @author laurent
 *
 */
public class CommunityTermHandle extends AAccessControledData<ICommunityTerm> implements ICommunityTermHandle  {

	private Log log = LogFactory.getLog(CommunityTermHandle.class);
	
	
	public CommunityTermHandle(IUserProfileData refUserProfile, ICommunityTerm refCommunityTermData) {
		super(refUserProfile,refCommunityTermData);
	}

	@Override
	public TermVocabularySet getVocabulary() {
		this.checkReadableByUser();
		return this.getRefData().getVocabulary(this.getUserProfile());
	}
	

	@Override
	public int getTermId() {
		this.checkReadableByUser();
		return this.getRefData().getTermId();
	}




	@Override
	public void setTermId(int termId) {
		this.checkWritableByUser();
		this.getRefData().setTermId(termId);		
	}




	@Override
	public int getDatatypeId() {
		this.checkReadableByUser();
		return this.getRefData().getDatatypeId();
	}




	@Override
	public void setDatatypeId(int dataTypeId) {
		this.checkWritableByUser();
		this.getRefData().setDatatypeId(dataTypeId);		
	}




	@Override
	public boolean isEnum() {
		this.checkReadableByUser();
		return this.getRefData().isEnum();
	}




	@Override
	public void setEnum(boolean isEnum) {
		this.checkWritableByUser();
		this.getRefData().setEnum(isEnum);		
	}




	@Override
	public String getDatatypeName() {
		this.checkReadableByUser();
		return this.getRefData().getDatatypeName();
	}

	@Override
	public String getIdName() {
		this.checkReadableByUser();
		return this.getRefData().getIdName();
	}

	@Override
	public void setIdName(String newIdCode) {
		this.checkWritableByUser();
		this.getRefData().setIdName(newIdCode);
	}

	@Override
	public TermVocabularySet getVocabulary(IUserProfileData activeUser) {
		return this.getRefData().getVocabulary(activeUser);
	}

/* Clone
	@Override
	public ICommunityTermData clone() throws CloneNotSupportedException {
		return this.getRefData().clone();
	}
*/

	@Override
	public List<TermVocabularySet> getVocabularySets() {
		return this.getRefData().getVocabularySets();
	}


	@Override
	public void setVocabularySets(List<TermVocabularySet> vocabularySets) {
		this.getRefData().setVocabularySets(vocabularySets);
	}


	@Override
	public void addVocabularySet(TermVocabularySet vocabularySet) {
		this.getRefData().addVocabularySet(vocabularySet);		
	}


	@Override
	public void setCommunityId(int communityId) {
		this.getRefData().setCommunityId(communityId);		
	}

	@Override
	public ICommunity getCommunityData() {
		return this.getRefData().getCommunityData();
	}
	
	@Override
	public void checkDataDBCompliance() throws DataAccessConstraintException {
		this.getRefData().checkDataDBCompliance();		
	}
	


}
