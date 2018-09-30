package metaindex.test;


import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.StrutsSpringTestCase;

import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityHandle;
import metaindex.data.community.ICommunity;
import metaindex.data.community.beans.BeanUpdateCommunityAddTermProcess;
import metaindex.data.community.beans.BeanUpdateCommunityDeleteTermProcess;
import metaindex.data.community.beans.BeanUpdateCommunityUpdateTermProcess;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.userprofile.UserProfileData;

import com.opensymphony.xwork2.ActionProxy;

public class TestCommunityTerms extends StrutsSpringTestCase {

	private static Log log = LogFactory.getLog(TestCommunity.class);
	//private DBUsersAccessor userprofileAccessor;
	
	private final Integer LONG_TEXT_DATATYPE_ID = 5;
	  /**
	   * Create a good new Test Community 
	   */
	  public void testCreateTerm() throws Exception {
		  
		  String newTermIdName=(String)TestSuiteMetaindex.testData.get("term.idName");
		  Integer newTermDatatypeId=LONG_TEXT_DATATYPE_ID; 
		  String communityIdName="Test Community";
		  
		  request.setParameter("idName", communityIdName);		  
		  request.setParameter("newTermIdName", newTermIdName);
		  request.setParameter("newTermDataTypeId", newTermDatatypeId.toString());
		  
		  ActionProxy proxy = getActionProxy("/addCommunityTermProcess");
		  
		    
		  BeanUpdateCommunityAddTermProcess action = (BeanUpdateCommunityAddTermProcess) proxy.getAction();
		  action.setValidationActiveUser("testuser");
	      CommunitiesAccessor.reset();
		  ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		  action.setSelectedCommunity(new CommunityHandle(action.getLoggedUserProfile(),c));
	      action.getSelectedCommunity().updateFull();
   
	        // Emulate OpenCommunity Action
	        action.getSelectedCommunity().setSelectedCatalog(action.getSelectedCommunity().getCatalogs().get(0));
	        		  
		  
	      String result = proxy.execute();
	        
	        assertEquals("Bad result while excuting createTerm test", 
	        				BeanProcessResult.BeanProcess_SUCCESS.toString(), result);
	        
	        assertNotNull("New term not created",
	        		action.getSelectedCommunity().getTermData(newTermIdName));
	    }
	  

	  /**
	   * Try to delete a term
	   */
	  public void testDeleteTerm() throws Exception {
			  
		  Integer termId=666;//corresponding to ID of "testing term"
		  String communityIdName="Test Community";
		  
		  request.setParameter("formTermId", termId.toString());
		  
		  
		  ActionProxy proxy = getActionProxy("/deleteCommunityTermProcess");
		    
		  BeanUpdateCommunityDeleteTermProcess action = (BeanUpdateCommunityDeleteTermProcess) proxy.getAction();
		  action.setValidationActiveUser("testuser");
	      CommunitiesAccessor.reset();
		  ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		  action.setSelectedCommunity(new CommunityHandle(action.getLoggedUserProfile(),c));
		  action.getSelectedCommunity().updateFull();
   
		  action.getSelectedCommunity().setSelectedCatalog(action.getSelectedCommunity().getCatalogs().get(0));
			
		  assertEquals(5,action.getSelectedCommunity().getTerms().size());
	      	      
	      String result = proxy.execute();
	        
      	  assertEquals("Result returned from executing the action was not successful while it should have been.", 
	        		BeanProcessResult.BeanProcess_SUCCESS.toString(),result);
	        
      	  assertEquals(4,action.getSelectedCommunity().getTerms().size());
      	  
      	  // check that term does not exist anymore
      	  assertNull(action.getSelectedCommunity().getTermDataById(666));
      	  
	    }
	  

	  /**
	   * Try to get data from existing community term, modify and update it into database
	   */
	  public void testUpdateTerm() throws Exception {
		  
		  
		  String termIdName=(String)TestSuiteMetaindex.testData.get("term.existingIdName");
		  String newTermDatatypeId=(String)TestSuiteMetaindex.testData.get("term.newTypeId");
		  String communityIdName=(String)TestSuiteMetaindex.testData.get("community.name");
		  String newTermNameTraduction="Blabla pouet"; 
		  request.setParameter("formTermIdName", termIdName);		  
		  request.setParameter("selectedCommunity.termsMap['"+termIdName+"'].datatypeId", newTermDatatypeId);
		  request.setParameter("selectedCommunity.termsMap['"+termIdName+"'].vocabulary.termNameTraduction", newTermNameTraduction);
		  ActionProxy proxy = getActionProxy("/updateCommunityTermProcess");
		    
		  BeanUpdateCommunityUpdateTermProcess action = (BeanUpdateCommunityUpdateTermProcess) proxy.getAction();
		  action.setValidationActiveUser("testuser");
	      CommunitiesAccessor.reset();
		  ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		  action.setSelectedCommunity(new CommunityHandle(action.getLoggedUserProfile(),c));
	      action.getSelectedCommunity().updateFull();
   	      action.getSelectedCommunity().setSelectedCatalog(action.getSelectedCommunity().getCatalogs().get(0));
		  
   	      // ensure user language is EN in database (might have been changed by previous tests
   	      action.getLoggedUserProfile().setGuiLanguageId(UserProfileData.DEFAULT_LANG_ID);
   	      action.getLoggedUserProfile().commit(action.getLoggedUserProfile());
		  assertEquals(UserProfileData.DEFAULT_LANG_ID,action.getLoggedUserProfile().getGuiLanguageId());
		  
		  String result = proxy.execute();		  
		  		  
	  		assertEquals("Bad result while excuting updateTerm test", 
	        	BeanProcessResult.BeanProcess_SUCCESS.toString(),result);
		    
		    // reload community data
	  		action.getSelectedCommunity().update();
	  			  		 
	  		assertEquals("Updated datatype id for term '"+termIdName+"' not as expeced",
	  				new Integer(newTermDatatypeId),
	  				new Integer(action.getSelectedCommunity().getTermData(termIdName).getDatatypeId()));
	        
	  		assertEquals("Updated term name traduction for term '"+termIdName+"' not as expeced",
	  				newTermNameTraduction,
	  				action.getSelectedCommunity().getTermData(termIdName).getVocabulary(action.getLoggedUserProfile()).getTermNameTraduction());
	  		
	    }


	  /**
	   * Try to create a duplicate term and ensure that this does not work 
	   */
	  public void testCreateDuplicateTerm() throws Exception {
			  
		  String newTermIdName="Title";
		  Integer newTermDatatypeId=10;
		  String communityIdName="Test Community";
		  
		  request.setParameter("idName", communityIdName);		  
		  request.setParameter("newTermIdName", newTermIdName);
		  request.setParameter("newTermDataTypeId", newTermDatatypeId.toString());
		  
		  ActionProxy proxy = getActionProxy("/addCommunityTermProcess");
		    
		  BeanUpdateCommunityAddTermProcess action = (BeanUpdateCommunityAddTermProcess) proxy.getAction();
		  action.setValidationActiveUser("testuser");
	      CommunitiesAccessor.reset();
		  ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		  action.setSelectedCommunity(new CommunityHandle(action.getLoggedUserProfile(),c));
	      action.getSelectedCommunity().updateFull();
   
	      action.getSelectedCommunity().setSelectedCatalog(action.getSelectedCommunity().getCatalogs().get(0));
			  
	      String result = proxy.execute();
	        
	        assertEquals("Result returned form executing the action was success but it should not have been.", 
	        		BeanProcessResult.BeanProcess_DBERROR.toString(),result);
	        
	    }
	 


}
