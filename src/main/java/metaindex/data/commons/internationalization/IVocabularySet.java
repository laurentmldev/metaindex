package metaindex.data.commons.internationalization;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.utils.IIdentifiable;

public interface IVocabularySet extends IIdentifiable<Integer> {
		public void setId(Integer dbId);
		public Integer getGuiLanguageId() ;
		public void setGuiLanguageId(Integer languageId);
		
		public void setVocabularyEntry(String entryName, String entryTranslation);
		public String getVocabularyEntry(String entryName);
}
