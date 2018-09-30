package metaindex.data.community;

import java.util.List;
import java.util.Map;

import metaindex.data.IBufferizedData;
import metaindex.data.IGenericMetaindexData;
import metaindex.data.IMultiLanguageData;
import metaindex.data.element.Element;
import metaindex.data.element.ElementHandle;
import metaindex.data.element.IElement;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.IDBAccessFactoryManager;
import metaindex.dbaccess.IDBAccessedData;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;

/**
 * Bean storing community data
 * @author laurent
 *
 */
public interface IGenericCommunity extends IDBAccessedData,IDBAccessFactoryManager {

	public Integer getCommunityId();
	public void setCommunityID(int communityID);
	public String getIdName();
	public void setIdName(String idName);
	public String getCreatorName();
	public void setCreatorName(String creatorName);
	public int getGroupId();	
	public void setGroupId(int groupId);
	public String getGroupName();
	public void setGroupName(String groupName);
	public void setVocabularySets(List<CommunityVocabularySet> vocabularySets);
	public void addVocabularySet(CommunityVocabularySet vocabularySet);
	public int getNbElements();
	public List<CommunityVocabularySet> getVocabularySets();
	List<CommunityDatatype> getDatatypes();
	public void setDatatypes(List<CommunityDatatype> datatypes);
	public String getDatatypeName(int datatypeId);
	
	
	
}
