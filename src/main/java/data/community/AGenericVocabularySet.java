package metaindex.data.community;

/**
 * Generic part of a vocabulary set.
 * It contains at least a guilanguage_id, plus one or several data fields
 * to be added in children classes
 * @author laurent
 *
 */
public abstract class AGenericVocabularySet {

	/** Language ID associated with this vocabulary set */
	private int guiLanguageID;

	public int getGuiLanguageID() {
		return guiLanguageID;
	}

	public void setGuiLanguageID(int guiLanguageID) {
		this.guiLanguageID = guiLanguageID;
	}
	
	
}
