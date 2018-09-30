package metaindex.data.userprofile;

import java.util.List;

import com.opensymphony.xwork2.ActionSupport;

import metaindex.data.IBufferizedData;
import metaindex.data.IGenericMetaindexData;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.community.CommunityHandle;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunityHandle;
import metaindex.data.element.IElementHandle;
import metaindex.dbaccess.IDBAccessFactoryManager;
import metaindex.dbaccess.IDBAccessedData;
import metaindex.dbaccess.IDataAccessAware;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;


/**
 * Java object container for users DB table.
 * Retrieve also String info of corresponding foreign keys (guilanguage and guitheme).
 * @author Laurent ML
 */
public interface IUserProfileData extends IDataAccessAware,IDBAccessedData,IDBAccessFactoryManager,IBufferizedData
{

	public Integer getUserId();
	public void setUserId(Integer id);
	
	public boolean isLoggedIn();
	
	
	public void logIn(IUserProfileData activeUser, String username);
	public void logIn() throws DataAccessConstraintException;
	
	/**
	 * Get text from given text ID based on current Locale configuration
	 * @param textId the ID of the text to retrieve
	 * @return the wanted text (in corresponding language) or the ID if not found or not reachable
	 */
	public String getText(String textId);
	public void setCurrentActionSupport(ActionSupport a);
	
	public String getSessionId();
	public void setSessionId(String sId);
	
	public String getUsername();
	public void setUsername(String username);
	
	public String getEmail();      
    public void setEmail(String email);  

    public String getPassword();
    public void setPasswordAndEncrypt(String password);  

    public void setGuiLanguageId(int guilanguageid);
    public int getGuiLanguageId();
    public String getGuiLanguageShort();
    public String getGuiLanguage();          

    public void setGuiThemeId(int guithemeid);      
    public int getGuiThemeId();
    public String getGuiThemeShort();
    public String getGuiTheme();
    
    
    
    public List<GuiLanguageData> getGuiLanguages();
    public List<GuiThemeData> getGuiThemes();
    
    public ICommunityHandle getSelectedCommunity();
    public void setSelectedCommunity(ICommunityHandle c);
    public boolean isEnabled();
    public ICatalogHandle getSelectedCatalog();
    public IElementHandle getSelectedElement();
    
    public void quitSelectedCommunity();
    
	public List<ICommunityHandle> getUserCommunities(IUserProfileData profile);
	public List<ICommunityHandle> getAllCommunities();
	
    
}
