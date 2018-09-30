package metaindex.data;

import java.util.List;

import metaindex.data.community.AGenericVocabularySet;
import metaindex.data.userprofile.IUserProfileData;

/**
 * BeanData specialization for components having dynamic multi-language translations stored in database
 * @author laurent
 *
 * @param <SpecificVocabularySet> Class describing the various fields translations for a given language
 */
public  interface IMultiLanguageData <SpecificVocabularySet extends AGenericVocabularySet> {

	
	
	public List<SpecificVocabularySet> getVocabularySets() ;

	public void setVocabularySets(List<SpecificVocabularySet> vocabularySets) ;
	
	public void addVocabularySet(SpecificVocabularySet vocabularySet) ;
	
	public abstract SpecificVocabularySet getVocabulary(IUserProfileData activeUser);
	public String getIdName() ;

	public void setIdName(String idName) ;

	

}
