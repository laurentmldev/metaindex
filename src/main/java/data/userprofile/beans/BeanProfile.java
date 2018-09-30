package metaindex.data.userprofile.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;
import org.springframework.transaction.PlatformTransactionManager;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

import metaindex.data.beans.AMetaindexBean;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityHandle;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunityHandle;
import metaindex.data.element.IElementHandle;
import metaindex.data.userprofile.GuiLanguageData;
import metaindex.data.userprofile.GuiThemeData;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDataAccessAware;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IGenericEncodable.UnableToPopulateException;
    
/**
 * Common Bean for all the 'Profile' JSP pages (profile,createProfile, editProfile).
 * @author Laurent ML
 */
public class BeanProfile extends AMetaindexBean implements IDataAccessAware,Preparable,IUserProfileData {  
  
	private static final long serialVersionUID = -8112077614648473484L;
	private Log log = LogFactory.getLog(BeanProfile.class);

	
	@Override
  	public String execute() throws Exception {
		return BeanProcessResult.BeanProcess_SUCCESS.toString();
	}

	@Override
	public String getUsername() {		
		return this.getLoggedUserProfile().getUsername();
	}

	@Override
	public void setUsername(String username) {
		this.getLoggedUserProfile().setUsername(username);
	}

	@Override
	public String getEmail() {
		return this.getLoggedUserProfile().getEmail();
	}

	@Override
	public void setEmail(String email) {
		this.getLoggedUserProfile().setEmail(email);
	}

	@Override
	public void setPasswordAndEncrypt(String password) {
			this.getLoggedUserProfile().setPasswordAndEncrypt(password);
	}
	public void setPassword(String password) {
		this.getLoggedUserProfile().setPasswordAndEncrypt(password);
	}

	@Override
	public int getGuiLanguageId() {
		return this.getLoggedUserProfile().getGuiLanguageId();
	}
	@Override
	public String getGuiLanguage() {
		return this.getLoggedUserProfile().getGuiLanguage();
	}
	@Override
	public String getGuiLanguageShort() {
		return this.getLoggedUserProfile().getGuiLanguageShort();
	}

	@Override
	public void setGuiLanguageId(int guilanguageid) {
		this.getLoggedUserProfile().setGuiLanguageId(guilanguageid);
	}

	@Override
	public int getGuiThemeId() {
		return this.getLoggedUserProfile().getGuiThemeId();
	}
	@Override
	public String getGuiTheme() {
		return this.getLoggedUserProfile().getGuiTheme();
	}
	@Override
	public String getGuiThemeShort() {
		return this.getLoggedUserProfile().getGuiThemeShort();
	}

	@Override
	public void setGuiThemeId(int guithemeid) {
		this.getLoggedUserProfile().setGuiThemeId(guithemeid);
	}

	@Override
	public List<GuiLanguageData> getGuiLanguages() {
		return this.getLoggedUserProfile().getGuiLanguages();
	}
	@Override
	public List<GuiThemeData> getGuiThemes() {
		return this.getLoggedUserProfile().getGuiThemes();
	}
	    
	public List<ICommunityHandle> getUserCommunities() 
	{
		try {
			List<ICommunityHandle> result = this.getLoggedUserProfile().getUserCommunities(this.getLoggedUserProfile());			
			return result;
		} catch (DataAccessErrorException | DataAccessConstraintException e) {
			this.addActionError("A problem occured while retrieving your communities, sorry.");
			return new ArrayList<ICommunityHandle>();
		}
	}
	   
	/**
	 * Get the list of communities the user do not belong to
	 * @return list of communities
	 */    
	public List<ICommunityHandle> getOtherCommunities() 
	{
		List<ICommunityHandle> result = new ArrayList<ICommunityHandle>();
		
		try {			
			List<ICommunityHandle> myCommunities = getUserCommunities();
			List<ICommunityHandle> allCommunities = this.getLoggedUserProfile().getAllCommunities();
			Iterator<ICommunityHandle> itAll = allCommunities.iterator();
			
			// go through all the communities
			while (itAll.hasNext()) 
			{
				ICommunityHandle cur = (ICommunityHandle) itAll.next();
				Iterator<ICommunityHandle> itMine = myCommunities.iterator();
				boolean isMine=false; 
				// checking if the current user is already registered in the current community,
				// this could probably improved with a nicer DB SQL request
				while(itMine.hasNext()) 
				{
					ICommunityHandle curMine=itMine.next();
					if (curMine.getIdName().equals(cur.getIdName())) { isMine=true;continue; }
				}
				// if user is not registered in the current community, then add it to the result 
				if (!isMine) { result.add(cur); }
			}
			return result;
		} catch (DataAccessErrorException | DataAccessConstraintException e) {
			this.addActionError("A problem occured while retrieving other communities, sorry.");
			return new ArrayList<ICommunityHandle>();
		}
	}


	@Override
	public void invalidate() {
		this.getLoggedUserProfile().invalidate();		
	}


	@Override
	public void commit(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		this.getLoggedUserProfile().commit(activeUser);
	}


	@Override
	public void commitFull(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		this.getLoggedUserProfile().commitFull(activeUser);
	}


	@Override
	public void update(IUserProfileData activeUser)
			throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
		this.getLoggedUserProfile().update(activeUser);
	}


	@Override
	public void updateFull(IUserProfileData activeUser)
			throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
		this.getLoggedUserProfile().updateFull(activeUser);
	}


	@Override
	public void create(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		this.getLoggedUserProfile().create(activeUser);
	}


	@Override
	public void delete(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		this.getLoggedUserProfile().delete(activeUser);
	}


	@Override
	public boolean isSynchronized() {
		return this.getLoggedUserProfile().isSynchronized();
	}


	/* Clone
	@Override
	public IUserProfileData clone() throws CloneNotSupportedException {
		return this.getLoggedUserProfile().clone();
	}*/


	@Override
	public String getPassword() {
		return this.getLoggedUserProfile().getPassword();
	}
	

	@Override
	public boolean isEnabled() {
		return this.getLoggedUserProfile().isEnabled();
	}


	@Override
	public IElementHandle getSelectedElement() {
		return this.getLoggedUserProfile().getSelectedElement();
	}


	@Override
	public String getSessionId() {
		return this.getLoggedUserProfile().getSessionId();
	}


	@Override
	public void setSessionId(String sId) {
		this.getLoggedUserProfile().setSessionId(sId);
		
	}


	@Override
	public void quitSelectedCommunity() {
		this.getLoggedUserProfile().quitSelectedCommunity();
	}


	@Override
	public List<ICommunityHandle> getUserCommunities(IUserProfileData profile) {
		return this.getLoggedUserProfile().getUserCommunities(profile);
	}


	@Override
	public List<ICommunityHandle> getAllCommunities() {
		return this.getLoggedUserProfile().getAllCommunities();
	}

	@Override
	public void populate(IUserProfileData activeUser, JSONObject json, POPULATE_POLICY policy) throws UnableToPopulateException {
		this.getLoggedUserProfile().populate(activeUser, json, policy);		
	}


	@Override
	public DataSource getDataSource() {
		return this.getLoggedUserProfile().getDataSource();
	}


	@Override
	public void setDataSource(DataSource ds) throws BeanDataException {
		this.getLoggedUserProfile().setDataSource(ds);
		
	}


	@Override
	public PlatformTransactionManager getTxManager() {
		return this.getLoggedUserProfile().getTxManager();
	}


	@Override
	public void setDataAccess(ADataAccessFactory dataAccess) throws DataAccessErrorException {
		this.getLoggedUserProfile().setDataAccess(dataAccess);		
	}


	@Override
	public ADataAccessFactory getDataAccess() {
		return this.getLoggedUserProfile().getDataAccess();
	}


	@Override
	public void populateFromJson(IUserProfileData activeUser, JSONObject json,
			metaindex.dbaccess.IGenericEncodable.POPULATE_POLICY policy)
			throws metaindex.dbaccess.IGenericEncodable.UnableToPopulateException {
		this.getLoggedUserProfile().populate(activeUser, json, policy);
		
	}


	@Override
	public JSONObject encode() {
		return this.getLoggedUserProfile().encode();
	}


	@Override
	public Integer getUserId() {
		return this.getLoggedUserProfile().getUserId();
	}


	@Override
	public void setUserId(Integer id) {
		this.getLoggedUserProfile().setUserId(id);		
	}

	@Override
	public boolean isLoggedIn() {
		return this.getLoggedUserProfile().isLoggedIn();
	}

	@Override
	public void logIn(IUserProfileData activeUser, String username) {
		log.warn("'log-in' operation forbidden from bean object, ignored.");		
	}

	@Override
	public void setCurrentActionSupport(ActionSupport a) {
		log.warn("'setCurrentActionSupport' operation forbidden from bean object, ignored.");
		
	}

	@Override
	public void logIn() throws DataAccessConstraintException {
		log.warn("'log-in' operation forbidden from bean object, ignored.");
	}
	
}  
