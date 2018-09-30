package metaindex.test;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.StrutsSpringTestCase;

import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityHandle;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunityHandle;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.management.UsersAccessor;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.data.userprofile.beans.BeanCreateProfileProcess;
import metaindex.data.userprofile.beans.BeanEditProfileProcess;
import metaindex.data.userprofile.beans.BeanJoinCommunityProcess;

import com.opensymphony.xwork2.ActionProxy;

public class TestUserProfile extends StrutsSpringTestCase {

	private static Log log = LogFactory.getLog(TestUserProfile.class);
	
	  /**
	   * Create a good new Test user 
	   */
	  public void testCreateUser() throws Exception {
		  String newUserName="bis_"+(String)TestSuiteMetaindex.testData.get("userprofile.username");
		  String newEmail="bis_"+(String)TestSuiteMetaindex.testData.get("userprofile.email");
		  
		  // 1- Create a new test user
		  request.setParameter("username", newUserName);
		  request.setParameter("password", (String)TestSuiteMetaindex.testData.get("userprofile.password"));
		  request.setParameter("email",newEmail );
		  request.setParameter("guiLanguageId", TestSuiteMetaindex.testData.get("userprofile.guiLanguageId").toString());
		  request.setParameter("guiThemeId", TestSuiteMetaindex.testData.get("userprofile.guiThemeId").toString());
		  request.setParameter("acceptConditions", (String)TestSuiteMetaindex.testData.get("userprofile.acceptConditions"));
		    
		    ActionProxy proxy = getActionProxy("/CreateProfileProcess");
	 
	        BeanCreateProfileProcess action = (BeanCreateProfileProcess) proxy.getAction();
	        String result = proxy.execute();
	        
	        assertEquals("Result returned from executing the action was not success but it should have been.", 
	        				BeanProcessResult.BeanProcess_SUCCESS.toString(), result);

	        // 2- load testuser data and check contents
	        IUserProfileData dataUser = action.getLoggedUserProfile();
	        dataUser.update(action.getLoggedUserProfile());
	        assertEquals("Mismatching data user email", "bis_"+(String)TestSuiteMetaindex.testData.get("userprofile.email"),dataUser.getEmail());
	        assertEquals("Mismatching data user guiLanguageId",(Integer)TestSuiteMetaindex.testData.get("userprofile.guiLanguageId"), new Integer(dataUser.getGuiLanguageId()));
	        assertEquals("Mismatching data user guiThemeId", (Integer)TestSuiteMetaindex.testData.get("userprofile.guiThemeId"),new Integer(dataUser.getGuiThemeId()));
	        	        
	    }
	  
	  /**
	   * Create an existing Test user and generate error 
	   */
	  public void testCreateDuplicateUser() throws Exception {
		  
		  
		  // 1- Create an existing test user (it is already present in the default DB)
		  request.setParameter("username", (String)TestSuiteMetaindex.testData.get("userprofile.username"));
		  request.setParameter("password", (String)TestSuiteMetaindex.testData.get("userprofile.password"));
		  request.setParameter("email",(String)TestSuiteMetaindex.testData.get("userprofile.email") );
		  request.setParameter("guiLanguageId", TestSuiteMetaindex.testData.get("userprofile.guiLanguageId").toString());
		  request.setParameter("guiThemeId", TestSuiteMetaindex.testData.get("userprofile.guiThemeId").toString());
		  request.setParameter("acceptConditions", (String)TestSuiteMetaindex.testData.get("userprofile.acceptConditions"));
	        
		    ActionProxy action = getActionProxy("/CreateProfileProcess");
	        String result = action.execute();
	        
	        assertEquals("Result returned from executing the action was not success but it should have been.", 
	        				BeanProcessResult.BeanProcess_ConstraintERROR.toString(), result);
	        
	    }
	  
	  /**
	   * Modify personal data of an existing Test user
	   * @throws Exception
	   */ 
	  public void testEditUser() throws Exception {
		  
		  // 1- Create test user
		  request.setParameter("username", (String)TestSuiteMetaindex.testData.get("userprofile.username"));
		  request.setParameter("password", (String)TestSuiteMetaindex.testData.get("userprofile.password"));
		  request.setParameter("email", (String)TestSuiteMetaindex.testData.get("userprofile.email"));
		  request.setParameter("guiLanguageId", "2");
		  request.setParameter("guiThemeId", "2");

		  CommunitiesAccessor.reset();
		  UsersAccessor.reset();
	        
		    ActionProxy proxy = getActionProxy("/editProfileProcess");
	 
		    BeanEditProfileProcess action = (BeanEditProfileProcess) proxy.getAction();
		    action.setValidationActiveUser("testuser");
		    action.setUsername((String)TestSuiteMetaindex.testData.get("userprofile.username"));
		    action.setPassword((String)TestSuiteMetaindex.testData.get("userprofile.password"));
		    action.setEmail((String)TestSuiteMetaindex.testData.get("userprofile.email"));
		    action.setGuiLanguageId((Integer)TestSuiteMetaindex.testData.get("userprofile.guiLanguageId"));
		    action.setGuiThemeId((Integer)TestSuiteMetaindex.testData.get("userprofile.guiThemeId"));
		    
		    String result = proxy.execute();
	        
	        assertEquals("Result returned from executing the action was not success but it should have been.", 
	        											BeanProcessResult.BeanProcess_SUCCESS.toString(), result);
	        
	        // 2- load testuser data and check contents
	        IUserProfileData dataUser = action.getLoggedUserProfile();
	        dataUser.update(action.getLoggedUserProfile());
	        
	        assertEquals("Mismatching data user email", (String)TestSuiteMetaindex.testData.get("userprofile.email"),dataUser.getEmail());
	        assertEquals("Mismatching data user guiLanguageId",2, dataUser.getGuiLanguageId());
	        assertEquals("Mismatching data user guiThemeId", 2,dataUser.getGuiThemeId());		
	    }
		
	  /**
	   * Join an existing community 
	   */
	  public void testJoinCommunity() throws Exception {
		  
		  String username=(String)TestSuiteMetaindex.testData.get("userprofile.username2");
		  String communityIdName=(String)TestSuiteMetaindex.testData.get("community.name");
		  String communityGroupname=(String)TestSuiteMetaindex.testData.get("community.groupname");
		  
		  // 1- Fill data descriing which community to join
		  request.setParameter("communityIdName", communityIdName);
		  request.setParameter("communityGroupName", communityGroupname);
		  
		  ActionProxy proxy = getActionProxy("/joinCommunityProcess");			 
	 
		  	CommunitiesAccessor.reset();
		  	UsersAccessor.reset();
		  	
	        BeanJoinCommunityProcess action = (BeanJoinCommunityProcess) proxy.getAction();
	        action.setValidationActiveUser(username);
	        
	        List<ICommunityHandle> list = 
	        		action.getLoggedUserProfile().getUserCommunities(action.getLoggedUserProfile());
	        assertEquals("User '"+username+"' should belong to zero communities",0,list.size());
	            				
	        String result = proxy.execute();
	        
	        assertEquals("Result returned from executing the action was not success but it should have been.", 
	        				BeanProcessResult.BeanProcess_SUCCESS.toString(), result);

	        // check that after having joined it, the user now belong to one community instead of 0
	        list = action.getLoggedUserProfile().getSelectedCommunity().getCommunities();
	        assertEquals("User '"+username+"' should belong to zero communities",1,list.size());
	        
	    }
	
}
