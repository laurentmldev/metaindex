package metaindex.test;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.struts2.StrutsSpringTestCase;

import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.catalog.AllElementsCatalog;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityHandle;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunityHandle;
import metaindex.data.community.beans.BeanCommunityNextElementDataProcess;
import metaindex.data.community.beans.BeanCreateCommunityProcess;
import metaindex.data.community.beans.BeanOpenCommunityProcess;
import metaindex.data.community.beans.BeanUpdateCommunityProcess;
import metaindex.data.element.ElementHandle;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.userprofile.GuiLanguageData;
import metaindex.data.userprofile.UserProfileData;

import com.opensymphony.xwork2.ActionProxy;

public class TestCommunity extends StrutsSpringTestCase {

	private static Log log = LogFactory.getLog(TestCommunity.class);
	//private DBUsersAccessor userprofileAccessor;
	
	  /**
	   * Create a good new Test Community 
	   */
	  public void testCreateCommunity() throws Exception {
		  
		  
		  String newIdName="Other Test Community Blabla ! ;)";
		  //String newCreatorUserId="21";
		  
		  // 1- Create a new community
		  request.setParameter("idName", newIdName);
		  
		  ActionProxy proxy = getActionProxy("/createCommunityProcess");
	 
	        BeanCreateCommunityProcess action = (BeanCreateCommunityProcess) proxy.getAction();
	        action.setValidationActiveUser("testuser");
	        
	        CommunitiesAccessor.reset();
			
	        String result = proxy.execute();
	        
	        assertEquals("Result returned from executing the action was not success but it should have been.", 
	        								BeanProcessResult.BeanProcess_SUCCESS.toString(), result);
	        
	        assertNotNull(CommunitiesAccessor.getCommunity(newIdName));
	    }
	  

	  /**
	   * Try to create a duplicate community and check thatthis does not work 
	   */
	  public void testCreateDuplicateCommunity() throws Exception {
		  String newIdName="Test Community";
		  //String newCreatorUserId="21";
		  
		  // 1- Create a new community
		  request.setParameter("idName", newIdName);
		  
		  ActionProxy proxy = getActionProxy("/createCommunityProcess");
	 
	        BeanCreateCommunityProcess action = (BeanCreateCommunityProcess) proxy.getAction();
	        action.setValidationActiveUser("testuser");
	        CommunitiesAccessor.reset();
	        
	        String result = proxy.execute();
	        
	        assertEquals("Result returned from executing the action was success but it should have been.", 
	        				BeanProcessResult.BeanProcess_ConstraintERROR.toString(), result);
	        
	    }

	  /**
	   * Try to get data from existing community, modify and update it into database
	   */	  
	  public void testUpdateCommunity() throws Exception {
		  String idName="Test Community";
		  String elementModifiedTranslation="Test Translation Bipbip";
	        
		  request.setParameter("selectedCommunity.idName", idName);
		  request.setParameter("selectedCommunity.vocabularySets[0].elementTraduction", elementModifiedTranslation);
		  
		  ActionProxy proxy = getActionProxy("/updateCommunityProcess");
	 
		  	BeanUpdateCommunityProcess action = (BeanUpdateCommunityProcess) proxy.getAction();
		  	action.setValidationActiveUser("testuser");
		  	CommunitiesAccessor.reset();
		  	ICommunity c = CommunitiesAccessor.getCommunity(idName);
		  	action.setSelectedCommunity(new CommunityHandle(action.getLoggedUserProfile(),c));
		  	action.getSelectedCommunity().update();
		  	
		  	List<GuiLanguageData> guiLanguagesList = 
		  			action.getSelectedCommunity().getDataAccess().getNewGuiLanguagesAccessor(
		  					action.getSelectedCommunity().getDataSource(),
		  					action.getSelectedCommunity().getTxManager()).getGuiLanguagesList();
		  	
		  	int nbLanguages=guiLanguagesList.size();
		  	
	        String result = proxy.execute();
	        
	        assertEquals("Result returned from executing the action was not success but should have been.", 
	        				BeanProcessResult.BeanProcess_SUCCESS.toString(), result);
	        
	        assertEquals("Community data did not retrieve "+nbLanguages+" vocabulary sets as defined in guilanguages table.", 
	        		nbLanguages,action.getSelectedCommunity().getVocabularySets().size());
	        
	        assertEquals("Community idName not properly retrieved", 
	        		idName,action.getSelectedCommunity().getIdName());
    
	        action.execute();
	        
	        assertEquals("Community update not properly applied", 
	        		elementModifiedTranslation,
	        		action.getSelectedCommunity().getVocabularySets().get(0).getElementTraduction());
	    }
	  

	  /**
	   * Try to get data from existing community, modify and update it into database
	   */
	  public void testOpenCommunity() throws Exception {
		  String idName="Test Community";
		  request.setParameter("idName", idName);
		  
		  ActionProxy proxy = getActionProxy("/openCommunity");
	 
		  	BeanOpenCommunityProcess action = (BeanOpenCommunityProcess) proxy.getAction();
		  	action.setValidationActiveUser("testuser");
		  	CommunitiesAccessor.reset();
		  	ICommunity c = CommunitiesAccessor.getCommunity(idName);
		  	action.setSelectedCommunity(new CommunityHandle(action.getLoggedUserProfile(),c));
		  	action.getSelectedCommunity().updateFull();
		  	
		  	 String result = proxy.execute();
		  	 
		  	assertEquals("Result returned from executing the action was not success but should have been.", 
    				BeanProcessResult.BeanProcess_SUCCESS.toString(), result);

		  	// Default test database at initial state
	        assertEquals("Nb Elements found not matching expected value while executing the action.", 
	        		5,action.getSelectedCommunity().getElements().size());
	        
	        // Check order of elements
	        assertEquals("Position of element 1000 is not as expected", 
	        		new Integer(1000),action.getSelectedCommunity().getElements().get(0).getElementId());
	        assertEquals("Position of element 1001 is not as expected", 
	        		new Integer(1001),action.getSelectedCommunity().getElements().get(1).getElementId());
	        assertEquals("Position of element 1666 is not as expected", 
	        		new Integer(1666),action.getSelectedCommunity().getElements().get(2).getElementId());
	        assertEquals("Position of element 1020 is not as expected", 
	        		new Integer(1020),action.getSelectedCommunity().getElements().get(3).getElementId());
	        assertEquals("Position of element 1010 is not as expected", 
	        		new Integer(1010),action.getSelectedCommunity().getElements().get(4).getElementId());
	        
	        // Check nb datasets of first element from the list (element 1000)
	        assertEquals("Nb Datasets of element '"+action.getSelectedCommunity().getElement(1000).getElementId()+"' not matching expected value while executing the action.", 
	        		3,action.getSelectedCommunity().getElements().get(0).getDatasets().size());
	        
	        //check number of metadata form first dataset from 1st element of the list (element 1000)
	        assertEquals("Nb Metadata found not matching expected value while executing the action.", 
	        		6,action.getSelectedCommunity().getElement(1000).getDatasets().get(0).getMetadata().size());
	        
	        // Check loaded test catalog
	        // only 4 since there is one 'template' element which is not par of the 'All' catalog used here
	        assertEquals("Nb elements found in the 'all' catalog", 
	        		4,action.getSelectedCommunity()
	        					.getCatalog(AllElementsCatalog.ALL_ELEMENTS_CATALOG_ID).getElementsCount());
		  	
	}

	  /**
	   * Try to create a duplicate community and check thatthis does not work 
	   */
	  public void testGetUserCommunities() throws Exception {
		  
		  String username="testuser";
		  Integer userId=19;// that's the user ID in our database
		  final int EXPECTED_NB_COMMUNITIES=3;
		  UserProfileData profile = new UserProfileData();
		  profile.setUsername(username);
		  profile.setUserId(userId);
		  
		  ICommunity c1 = new Community("dummyCommunity");
		  c1.create(profile);
		  ICommunity c2 = new Community("dummyCommunity2");
		  c2.create(profile);
		  List<ICommunityHandle> result= profile.getUserCommunities(profile);

	        assertEquals("Unable to found the good amount of user communities for user '"+username+"'", 
	        		EXPECTED_NB_COMMUNITIES, result.size());
	        
	    }
  
	  
	  /**
	   * Create a good new Test Community 
	   */	  
	  public void testNextElementData() throws Exception {
		  String communityIdName="Test Community";
		  String nextElementId="1001";
		  
		  
		  // 1- Create a new metadata
		  request.setParameter("nextElementId", nextElementId);
		  ActionProxy proxy = getActionProxy("/selectElement");
	 	  
	        BeanCommunityNextElementDataProcess action = (BeanCommunityNextElementDataProcess) proxy.getAction();
	        action.setValidationActiveUser("testuser");
	        CommunitiesAccessor.reset();
	        ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);		  			  	
	        action.setSelectedCommunity(new CommunityHandle(action.getLoggedUserProfile(),c));
	        action.getSelectedCommunity().updateFull();
	        action.getSelectedCommunity().setSelectedCatalog(action.getSelectedCommunity()
	        																			.getCatalog(AllElementsCatalog.ALL_ELEMENTS_CATALOG_ID));
	        	       
	        assertEquals("Initially Selected element was not as expected.", 
	        		new Integer(1000), action.getSelectedCommunity().getSelectedCatalog().getSelectedElement().getElementId());
	        
	        String result = proxy.execute();
	        assertEquals("Result returned form executing the action was not success but it should have been.", 
					BeanProcessResult.BeanProcess_SUCCESS.toString(), result);
	        
	        assertEquals("Selected next element was not as expected.", 
	        		new Integer(1001), action.getSelectedCommunity().getSelectedCatalog().getSelectedElement().getElementId());
	        
	    }	    
}
