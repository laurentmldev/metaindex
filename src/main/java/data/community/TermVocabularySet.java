package metaindex.data.community;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TermVocabularySet extends AGenericVocabularySet {

	private Log log = LogFactory.getLog(TermVocabularySet.class);
	/** Language ID associated with this vocabulary set */
	private int guiLanguageID;
	
	/** Term Name per language ID*/
	private String termNameTraduction = new String();
	
	/** Term Comment (Description) per language ID*/
	private String termCommentTraduction = new String();

	public int getGuiLanguageID() {
		return guiLanguageID;
	}

	public void setGuiLanguageID(int guiLanguageID) {
		this.guiLanguageID = guiLanguageID;
	}

	public String getTermNameTraduction() {
		return termNameTraduction;
	}

	public void setTermNameTraduction(String termName) {
		this.termNameTraduction = termName;
	}

	public String getTermCommentTraduction() {
		return termCommentTraduction;
	}

	public void setTermCommentTraduction(String termComment) {
		this.termCommentTraduction = termComment;
	}
	
	
	
}
