package metaindex.data.commons.internationalization;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import metaindex.data.commons.globals.Globals;

public abstract class AVocabularySet implements IVocabularySet {
	
	private Integer _id;
	private Integer _languageId=1;
	private Map<String,String> _translations = new HashMap<>();
	
	@Override
	public Integer getId() { return _id; }
	@Override
	public void setId(Integer dbId) { _id=dbId; }
	
	@Override
	public Integer getGuiLanguageId() { return _languageId; }
	@Override
	public void setGuiLanguageId(Integer languageId) { _languageId=languageId; }
	
	public String getGuiLanguageShortName() {
		return Globals.Get().getGuiLanguagesMgr().getGuiLanguage(this.getGuiLanguageId()).getShortname();
	}
	public String getGuiLanguageFullName() {
		return Globals.Get().getGuiLanguagesMgr().getGuiLanguage(this.getGuiLanguageId()).getName();
	}
	
	public String capitalize(String str) {
		String result="";
		Scanner lineScan = new Scanner(str); 
        while(lineScan.hasNext()) {
            String word = lineScan.next(); 
            if (result.length()>0) { result+=" "; }
            result += Character.toUpperCase(word.charAt(0)) + word.substring(1);; 
        }
        return result;
	}
	
	public void setVocabularyEntry(String entryName, String entryTranslation) { _translations.put(entryName, entryTranslation); }
	public String getVocabularyEntry(String entryName) { return _translations.get(entryName); }
}
