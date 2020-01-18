package metaindex.data.catalog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.commons.internationalization.AVocabularySet;

public class CatalogVocabularySet extends AVocabularySet {

	// shall be compliant with SQL db table guilanguages
	public static final Integer GUILANGUANGUE_ID_EN = 1;
	public static final Integer GUILANGUANGUE_ID_FR = 2;
	public static final Integer GUILANGUANGUE_ID_SP = 3;
	
	private Integer _catalogId;
	private Log log = LogFactory.getLog(CatalogVocabularySet.class);
	
	public static CatalogVocabularySet getDefaultLanguage(String langShortName) {
		CatalogVocabularySet defaultVoc = new CatalogVocabularySet();
		
		if (langShortName.equals("EN")) {
			defaultVoc.setGuiLanguageId(GUILANGUANGUE_ID_EN);
			defaultVoc.setName("Catalog");
			defaultVoc.setComment("A catalog");
			defaultVoc.setItem("item");
			defaultVoc.setItems("items");
			defaultVoc.setUser("user");
			defaultVoc.setUsers("users");
		} else if (langShortName.equals("FR")) {
			defaultVoc.setGuiLanguageId(GUILANGUANGUE_ID_FR);			
			defaultVoc.setName("Catalogue");
			defaultVoc.setComment("Un catalogue");
			defaultVoc.setItem("document");
			defaultVoc.setItems("documents");
			defaultVoc.setUser("utilisateur");
			defaultVoc.setUsers("utilisateurs");
		} else if (langShortName.equals("SP")) {
			defaultVoc.setGuiLanguageId(GUILANGUANGUE_ID_SP);			
			defaultVoc.setName("Catalog");
			defaultVoc.setComment("Un catalog");
			defaultVoc.setItem("documento");
			defaultVoc.setItems("documentos");
			defaultVoc.setUser("utilizador");
			defaultVoc.setUsers("utilizadores");
		} else {			
			return new CatalogVocabularySet();
		}
		return defaultVoc;
	}
	
	public CatalogVocabularySet() {
		this.setName("Catalog");
		this.setComment("A catalog");
		this.setItem("item");
		this.setItems("items");
		this.setUser("user");
		this.setUsers("users");
	}
	
	public CatalogVocabularySet(CatalogVocabularySet orig) {
		this.setId(orig.getId());
		this.setGuiLanguageId(orig.getGuiLanguageId());
		this.setCatalogId(orig.getCatalogId());
		
		this.setName(orig.getName());
		this.setComment(orig.getComment());
		this.setItem(orig.getItem());
		this.setItems(orig.getItems());
		this.setUser(orig.getUser());
		this.setUsers(orig.getUsers());		
	}
	@Override
	public String getName() {
		return this.getVocabularyEntry("name");
	}
	public void setName(String name) {
		this.setVocabularyEntry("name",name);
	}
	
	
	public String getComment() {
		return this.getVocabularyEntry("comment");
	}
	public void setComment(String comment) {
		this.setVocabularyEntry("comment",comment);
	}
	
	public String getItem() {
		return this.getVocabularyEntry("item");
	}
	public String getItemCap() { return capitalize(getItem()); }
	
	public void setItem(String item) {
		this.setVocabularyEntry("item",item);
	}
	public String getItems() {
		return this.getVocabularyEntry("items");
	}
	public String getItemsCap() { return capitalize(getItems()); }
	public void setItems(String items) { this.setVocabularyEntry("items",items); }	
	
	public String getUser() {
		return this.getVocabularyEntry("user");
	}
	public void setUser(String user) {
		this.setVocabularyEntry("user",user);
	}
	public String getUsers() {
		return this.getVocabularyEntry("users");
	}
	public void setUsers(String users) {
		this.setVocabularyEntry("users",users);
	}
	public Integer getCatalogId() {
		return _catalogId;
	}
	public void setCatalogId(Integer catalogId) {
		this._catalogId = catalogId;
	}
	
	
}
