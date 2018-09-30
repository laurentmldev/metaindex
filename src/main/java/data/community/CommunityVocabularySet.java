package metaindex.data.community;

public class CommunityVocabularySet extends AGenericVocabularySet {

	/** Language ID associated with this vocabulary set */
	private int guiLanguageID;
	

	/** Community Name per language ID*/
	private String communityNameTraduction = new String();
	
	/** Community Comment (Description) per language ID*/
	private String communityCommentTraduction = new String();
	/** Community description using some of the vocabulary traductions */
	private String communityVocabularyDescription = new String();
	
	/** Community vocabulary for 'Element' per language ID*/
	private String elementTraduction = new String();
	/** Community vocabulary for 'Elements' (plural) per language ID*/
	private String elementsTraduction = new String();
	
	/** Community vocabulary for 'Element' per language ID*/
	private String datasetTraduction = new String();
	/** Community vocabulary for 'Elements' (plural) per language ID*/
	private String datasetsTraduction = new String();
	
	/** Community vocabulary for 'Metadata' per language ID*/
	private String metadataTraduction = new String();
	/** Community vocabulary for 'Metadata' (plural) per language ID*/
	private String metadatasTraduction = new String();
	
	/** Community vocabulary for 'Catalog' per language ID*/
	private String catalogTraduction = new String();
	/** Community vocabulary for 'Catalog' (plural) per language ID*/
	private String catalogsTraduction = new String();
	
	/** Community vocabulary for 'User' per language ID*/
	private String userTraduction = new String();
	/** Community vocabulary for 'Users' (plural) per language ID*/
	private String usersTraduction = new String();
	
	/** Community vocabulary for 'UserGroup' per language ID*/
	private String userGroupTraduction = new String();
	/** Community vocabulary for 'UserGroups' (plural) per language ID*/
	private String userGroupsTraduction = new String();
	
	
	public int getGuiLanguageID() {
		return guiLanguageID;
	}
	public void setGuiLanguageID(int languageID) {
		this.guiLanguageID = languageID;
	}
	public String getCommunityNameTraduction() {
		return communityNameTraduction;
	}
	public void setCommunityNameTraduction(String nameTraduction) {
		this.communityNameTraduction = nameTraduction;
	}
	public String getCommunityCommentTraduction() {
		return communityCommentTraduction;
	}
	public void setCommunityCommentTraduction(String commentTraduction) {
		this.communityCommentTraduction = commentTraduction;
	}
	public String getCommunityVocabularyDescription() {
		
		String realText=communityVocabularyDescription;
		realText=realText.replaceAll("%element%", this.getElementTraduction());
		realText=realText.replaceAll("%elements%", this.getElementsTraduction());
		realText=realText.replaceAll("%user%", this.getUserTraduction());
		realText=realText.replaceAll("%users%", this.getUsersTraduction());
		realText=realText.replaceAll("%metadata%", this.getMetadataTraduction());
		realText=realText.replaceAll("%metadatas%", this.getMetadatasTraduction());
		realText=realText.replaceAll("%catalog%", this.getCatalogTraduction());
		realText=realText.replaceAll("%catalogs%", this.getCatalogsTraduction());
		
		return realText;
	}
	/**
	 * Set the description containing patterns to be replaced by the corresponding translation.
	 * Allowed patterns are : %user%,%users%,%element%,%elements%,%metadata%,%metadatas%,%catalog%,%catalogs%
	 * @param text the text containing the description (with patterns %xxx%). 
	 */
	public void setCommunityVocabularyDescription(String text) {
		communityVocabularyDescription=text;
	}
	public String getElementTraduction() {
		return elementTraduction;
	}
	public String getCapElementTraduction() {
		return elementTraduction.substring(0, 1).toUpperCase() + elementTraduction.substring(1);
	}
	public void setElementTraduction(String elementTraduction) {
		this.elementTraduction = elementTraduction;
	}
	public String getElementsTraduction() {
		return elementsTraduction;
	}
	public void setElementsTraduction(String elementsTraduction) {
		this.elementsTraduction = elementsTraduction;
	}
	public String getDatasetTraduction() {
		return datasetTraduction;
	}
	public void setDatasetTraduction(String datasetTraduction) {
		this.datasetTraduction = datasetTraduction;
	}
	public String getCapDatasetTraduction() { 
		return datasetTraduction.substring(0, 1).toUpperCase() + datasetTraduction.substring(1);
	}
	public String getDatasetsTraduction() {
		return datasetsTraduction;
	}
	public void setDatasetsTraduction(String datasetsTraduction) {
		this.datasetsTraduction = datasetsTraduction;
	}
	public String getMetadataTraduction() {
		return metadataTraduction;
	}
	public String getCapMetadataTraduction() {
		return metadataTraduction.substring(0, 1).toUpperCase() + metadataTraduction.substring(1);
	}
	public void setMetadataTraduction(String metadataTraduction) {
		this.metadataTraduction = metadataTraduction;
	}
	public String getMetadatasTraduction() {
		return metadatasTraduction;
	}
	public void setMetadatasTraduction(String metadatasTraduction) {
		this.metadatasTraduction = metadatasTraduction;
	}
	public String getCatalogTraduction() {
		return catalogTraduction;
	}
	public String getCapCatalogTraduction() {
		return catalogTraduction.substring(0, 1).toUpperCase() + catalogTraduction.substring(1);
	}
	public void setCatalogTraduction(String catalogTraduction) {
		this.catalogTraduction = catalogTraduction;
	}
	public String getCatalogsTraduction() { 
		return catalogsTraduction;
	}
	public String getCapCatalogsTraduction() { 
		return catalogsTraduction.substring(0, 1).toUpperCase() + catalogsTraduction.substring(1);
	}
	
	public void setCatalogsTraduction(String catalogsTraduction) {
		this.catalogsTraduction = catalogsTraduction;
	}
	public String getUserTraduction() {
		return userTraduction;
	}
	public void setUserTraduction(String userTraduction) {
		this.userTraduction = userTraduction;
	}
	public String getUsersTraduction() {
		return usersTraduction;
	}
	public void setUsersTraduction(String usersTraduction) {
		this.usersTraduction = usersTraduction;
	}
	public String getUserGroupTraduction() {
		return userGroupTraduction;
	}
	public void setUserGroupTraduction(String userGroupTraduction) {
		this.userGroupTraduction = userGroupTraduction;
	}
	public String getUserGroupsTraduction() {
		return userGroupsTraduction;
	}
	public void setUserGroupsTraduction(String userGroupsTraduction) {
		this.userGroupsTraduction = userGroupsTraduction;
	}
	
}
