package metaindex.data.userprofile;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import metaindex.data.filter.IFilter;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogChatMsg;
import metaindex.data.commons.globals.guilanguage.IGuiLanguage;
import metaindex.data.commons.globals.guitheme.IGuiTheme;
import toolbox.exceptions.DataProcessException;
import toolbox.patterns.observer.IObserver;
import toolbox.utils.IPeriodicProcess;
import toolbox.utils.IProcessingTask;


/**
 * Java object container for users DB table.
 * Retrieve also String info of corresponding foreign keys (guilanguage and guitheme).
 * @author Laurent ML
 */
public interface IUserProfileData extends ICatalogUser,IPlanUser,IObserver<IProcessingTask>,IPeriodicProcess
{
	
	// enum defined in SQL db too
	public enum USER_ROLE { ROLE_ADMIN, ROLE_USER, ROLE_OBSERVER };
	public USER_ROLE getRole();
	
	// enum defined in SQL db too for users (without ALL) and plans (with ALL) 
	public enum CATEGORY { PERSONAL,STUDENT_SEARCHER, NONPROFIT, ADMINISTRATION,BUSINESS,ALL };
	// different plan apply depending on user category
	public CATEGORY getCategory();
	public void setCategory(CATEGORY c);
	
	public void setId(Integer id);
	public void setName(String name);
	public void setRole(USER_ROLE role);
	
	public void setEnabled(Boolean enabled);
	
	public void setNickname(String nickName);
	;
	public String getPassword();
	public void setPasswordAndEncrypt(String clearPassword);
    public void setEncryptedPassword(String passwordHash);
    
    public Integer getGuiLanguageId();
    public String getGuiLanguageShortname();
    public IGuiLanguage getGuiLanguage();
    public Collection<IGuiLanguage> getGuiLanguages();
    public void setGuiLanguageId(Integer guilanguageid);
    /**
     * Get text corresponding to given textId, in correct language
     * depending on user preference.
     */
    public String getText(String textid, String... params);
    
    public Integer getGuiThemeId();
    public String getGuiThemeShortname();
    public void setGuiThemeId(Integer guithemeid);          
    public IGuiTheme getGuiTheme();
    public Collection<IGuiTheme> getGuiThemes();
    
    public Date getLastLoginDate();
    public void setLastLoginDate(Date date);
    
    public void setCurNbCatalogsCreated(Integer nbCatalogs);
	
	/// load user data and associated roles
	public void loadFullUserData() throws DataProcessException;
    // --------
    
    
    public IFilter getCurrentFilter();
    public void setCurrentFilter(Integer filterId);

    public Integer getCurrentDocumentId();
    public void setCurrentDocumentId(Integer docId);
    
    public void logIn() throws DataProcessException;
    public boolean isLoggedIn();
	public void logOut() throws DataProcessException;
    
	public String getHttpSessionId();
	public HttpSession getHttpSession();
	public void setHttpSession(HttpSession session);
	
	public String getWebsocketSessionId();
	public void setWebsocketSessionId(String sId);
	
	public IProcessingTask getProcessingTask(Integer processingTaskId);
	public void addProcessingTask(IProcessingTask processingTask);
	public void removeProccessingTask(Integer processingTaskId);
	public void notifyChange(IProcessingTask processingTask);
	
	// 3rd party apps links
	public String getStatisticsUrl();
	public String getStatisticsDiscoverUrl();
	
	//-------- user access rights by catalog
	public void setUserCatalogAccessRights(Integer catalogId,USER_CATALOG_ACCESSRIGHTS accessRights);
	
	//-------- customization params by catalog
	public void setUserCatalogKibanaIFrameHtml(Integer catalogId,String iFrameKibanaString);
	public String getUserCatalogKibanaIFrameHtml(Integer catalogId);
	public String getCurrentCatalogKibanaIFrameHtml();
	
	//-------- management of user local folder
	public String getLocalFsFilesPath();
	
	//----------
	public void sendEmail(String subject, String body) throws DataProcessException;
	public void sendEmailCCiAdmin(String subject, String body) throws DataProcessException;
	public void sendGuiChatMessage(ICatalog c, ICatalogChatMsg m);
	public void sendGuiErrorMessage(String msg);
	public void sendGuiErrorMessage(String msg,List<String> details);
	public void sendGuiInfoMessage(String msg);
	public void sendGuiWarningMessage(String msg);
	public void sendGuiSuccessMessage(String msg);
	public void sendGuiProgressMessage(Integer procId, String msg, Float pourcentage);	
	public void sendGuiProgressMessage(Integer procId, String msg, Float pourcentage, Boolean active);			
	
	/** set date of latest operation concerning items of current catalog */
	public void setItemsLastChangeDate(Date dateStr);
	/** get date of latest operation concerning items of current catalog 
	 * used to display items impacted by latest operation (like CSV import for example).
	 */
	public Date getItemsLastChangeDate();
	
	public void setLastUpdate(Date newDate);	
	public void setLastUpdateIfNewer(Date newDate);
	
	// used to ensure that on more than one session for a user at the same time
	String getRemoteAddress();
	void setRemoteAddress(String addr);	
	
	
}
