package metaindex.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import metaindex.data.community.AGenericVocabularySet;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityTerm;
import metaindex.data.community.CommunityVocabularySet;
import metaindex.data.community.TermVocabularySet;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;

/**
 * BeanData specialization for components having dynamic multi-language translations stored in database
 * @author laurent
 *
 * @param <SpecificVocabularySet> Class describing the various fields translations for a given language
 */
public  abstract class AMultiLanguageMetaindexData <T,SpecificVocabularySet extends AGenericVocabularySet> 
													extends AGenericMetaindexData<T> implements IMultiLanguageData<SpecificVocabularySet>{

	private Log log = LogFactory.getLog(AMultiLanguageMetaindexData.class);
	
	String idName="";
	List<SpecificVocabularySet> vocabularySets = new ArrayList<SpecificVocabularySet>();

	public AMultiLanguageMetaindexData(ADataAccessFactory dataAccessor) 
	{
		super(dataAccessor);
	}
	
	@Override
	public List<SpecificVocabularySet> getVocabularySets() {
		return vocabularySets;
	}
	
	
	/** Get the community vocabulary for the current selected language */
	@Override
	public abstract SpecificVocabularySet getVocabulary(IUserProfileData activeUser);

	@Override
	public void setVocabularySets(List<SpecificVocabularySet> vocabularySets) {
		this.vocabularySets.clear();
		this.vocabularySets.addAll(vocabularySets);		
	}
	
	@Override
	public void addVocabularySet(SpecificVocabularySet vocabularySet) {
		// TODO check if an existing set for the given language exist, or add corresponding DB constraint		
		this.vocabularySets.add(vocabularySet);
	}
	
	@Override
	public String getIdName() {
		return idName;
	}
	@Override
	public void setIdName(String idName) {
		this.idName = idName;
	}

}
