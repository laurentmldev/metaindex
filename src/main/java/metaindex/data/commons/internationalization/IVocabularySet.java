package metaindex.data.commons.internationalization;

import toolbox.utils.IIdentifiable;

public interface IVocabularySet extends IIdentifiable<Integer> {
		public void setId(Integer dbId);
		public Integer getGuiLanguageId() ;
		public void setGuiLanguageId(Integer languageId);
		
		public void setVocabularyEntry(String entryName, String entryTranslation);
		public String getVocabularyEntry(String entryName);
}
