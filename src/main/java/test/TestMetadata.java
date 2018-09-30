package metaindex.test;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.StrutsSpringTestCase;

import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityHandle;
import metaindex.data.community.ICommunity;
import metaindex.data.community.beans.BeanCreateCommunityProcess;
import metaindex.data.community.beans.BeanUpdateCommunityProcess;
import metaindex.data.dataset.IDatasetHandle;
import metaindex.data.element.Element;
import metaindex.data.element.IElementHandle;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.Metadata;
import metaindex.data.metadata.beans.BeanElementAddMetadataProcess;
import metaindex.data.metadata.beans.BeanElementDeleteMetadataProcess;
import metaindex.data.metadata.beans.BeanElementUpdateMetadataProcess;
import metaindex.data.userprofile.GuiLanguageData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;

import com.opensymphony.xwork2.ActionProxy;

public class TestMetadata extends StrutsSpringTestCase {

	private static Log log = LogFactory.getLog(TestCommunity.class);
	//private DBUsersAccessor userprofileAccessor;
	
	  /**
	   * Create a good new Test Community 
	   */
	  public void testCreateMetadata() throws Exception {
		  
		  String communityIdName="Test Community";
		  String newMetadataName="A third metadata";
		  String newMetadataComment="A comment for this test metadata";
		  String datasetId="1000"; // we know that our test database is built with a test dataset having its id=1000
		  String communityTermId="3";
		  String position="3";
		  String column="2";
		  
		  // 1- Create a new metadata
		  request.setParameter("formMetadataName", newMetadataName);
		  request.setParameter("formMetadataComment", newMetadataComment);
		  request.setParameter("formDatasetId", datasetId);
		  request.setParameter("formTermId", communityTermId);
		  request.setParameter("formColumn", column);
		  request.setParameter("formPosition", position);
		  
		  ActionProxy proxy = getActionProxy("/addMetadataProcess");
	 
	        BeanElementAddMetadataProcess processAction = (BeanElementAddMetadataProcess) proxy.getAction();
	        processAction.setValidationActiveUser("testuser");
	        
	        CommunitiesAccessor.reset();
		    ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		    
	        processAction.setSelectedCommunity(new CommunityHandle(processAction.getLoggedUserProfile(),c));
	        processAction.getSelectedCommunity().updateFull();
	        processAction.getSelectedCommunity().getSelectedCatalog().setSelectedElement(1000);
	        
	        assertEquals("Nb of found metadata (before adding one) was not as expected.", 
					6, processAction.getSelectedCommunity().getSelectedCatalog().getSelectedElement().getDatasets().get(0).getMetadata().size());
	        
	        String result = proxy.execute();
	        
	        assertEquals("Result returned form executing the action was not success but it should have been.", 
	        								BeanProcessResult.BeanProcess_SUCCESS.toString(), result);
	        
	        assertEquals("Nb of found metadata (after adding one) was not as expected.", 
					7, processAction.getSelectedCommunity().getSelectedCatalog().getSelectedElement().getDatasets().get(0).getMetadata().size());
	        
	    }
	  
		
	  /**
	   * Edit existing metadata 
	   */
	  public void testUpdateMetadata() throws Exception {
		  
		  String communityIdName="Test Community";
		  
		  // set a new value to the 'longString' field of the Metadata having id='1000' belonging to 
		  // dataset with id='1000' of currently selected element.
		  // Currently selected element is set up later below.
		  String metadataIdentification="selectedElement.datasetsMap['1000'].metadatasMap['1000'].longString";
		  String metadataValue="<p>This is a rich text replacing the existing one hehehe</p>";
		  
		  // 1- Create a new metadata
		  request.setParameter(metadataIdentification, metadataValue);
		  
		  CommunitiesAccessor.reset();
	      ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		    
		  ActionProxy proxy = getActionProxy("/updateMetadataProcess");
	 
	        BeanElementUpdateMetadataProcess processAction = (BeanElementUpdateMetadataProcess) proxy.getAction();
	        processAction.setValidationActiveUser("testuser");
	        processAction.setSelectedCommunity(new CommunityHandle(processAction.getLoggedUserProfile(),c));
	        processAction.getSelectedCommunity().updateFull();
     
	        processAction.getSelectedCommunity().getSelectedCatalog().setSelectedElement(1000);
	      	        
	        String result = proxy.execute();
	        
	        assertEquals("Result returned form executing the action was not success but it should have been.", 
	        								BeanProcessResult.BeanProcess_SUCCESS.toString(), result);
	        
	        assertEquals("Retrieved data not identical to what we expected", 
	        		metadataValue,processAction.getSelectedCommunity().getSelectedCatalog().getSelectedElement().getDataset(1000).getMetadata(1000).getLongString());
	        
	  }
	

		
	  /**
	   * Edit existing metadata as thumbnail and check impact on corresponding element 
	   */
	  public void testThumbnailMetadata() throws Exception {
		  
		  String communityIdName="Test Community";
		  
	        
		  // 1- Set a metadata as an image
		  String metadataTermIdentification="selectedElement.datasetsMap['1000'].metadatasMap['1000'].termId";
		  Integer metadataTermId=5; // 'Illustration' term in test DB (datatype is 'image')
		  request.setParameter(metadataTermIdentification, metadataTermId.toString());
		  		  		  
		  CommunitiesAccessor.reset();
		  ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);    
		  ActionProxy proxy = getActionProxy("/updateMetadataProcess");
		  BeanElementUpdateMetadataProcess processAction = (BeanElementUpdateMetadataProcess) proxy.getAction();
		  processAction.setValidationActiveUser("testuser");
		  processAction.setSelectedCommunity(new CommunityHandle(processAction.getLoggedUserProfile(),c));
		  processAction.getSelectedCommunity().updateFull();
		  processAction.getSelectedCommunity().getSelectedCatalog().setSelectedElement(1000);
		  String result = proxy.execute();
		    
		  assertEquals("Result returned form executing the action was not success but it should have been.", 
		    								BeanProcessResult.BeanProcess_SUCCESS.toString(), result);
		    
		  assertEquals("Retrieved data not identical to what we expected", 
				  metadataTermId,processAction.getSelectedCommunity().getSelectedCatalog().getSelectedElement().getDataset(1000).getMetadata(1000).getTermId());	  
		  
		  // 2- Set Url
		  // set a new value to the 'longString' field of the Metadata having id='1000' belonging to 
		  // dataset with id='1000' of currently selected element.
		  // Currently selected element is set up later below.
		  String metadatamageUrlIdentification="selectedElement.datasetsMap['1000'].metadatasMap['1000'].asImage.imageUrl";
		  String metadataImageUrl="http://website.com/my/uri/to/the/picture.png";
		  
		  CommunitiesAccessor.reset();
		  c = CommunitiesAccessor.getCommunity(communityIdName);    
		  request.setParameter(metadatamageUrlIdentification, metadataImageUrl);
		  proxy = getActionProxy("/updateMetadataProcess");
		  processAction = (BeanElementUpdateMetadataProcess) proxy.getAction();
		  processAction.setValidationActiveUser("testuser");
		  processAction.setSelectedCommunity(new CommunityHandle(processAction.getLoggedUserProfile(),c));
		  processAction.getSelectedCommunity().updateFull();
		  processAction.getSelectedCommunity().getSelectedCatalog().setSelectedElement(1000);
		  result = proxy.execute();
		
		  assertEquals("Result returned form executing the action was not success but it should have been.", 
										BeanProcessResult.BeanProcess_SUCCESS.toString(), result);
		
		  assertEquals("Retrieved data not identical to what we expected", 
		  metadataImageUrl,processAction.getSelectedCommunity().getSelectedCatalog().getSelectedElement().getDataset(1000).getMetadata(1000).getAsImage().getImageUrl());
	   
		  // 3- Check that corresponding Url has been set as thumbnail to the element
		  String metadatamageSetThumbnail="selectedElement.datasetsMap['1000'].metadatasMap['1000'].asImage.thumbnail";
		  String metadataIsThumbnail="true";
		  CommunitiesAccessor.reset();
		  c = CommunitiesAccessor.getCommunity(communityIdName);    
		  request.setParameter(metadatamageSetThumbnail, metadataIsThumbnail);
		  proxy = getActionProxy("/updateMetadataProcess");
		  processAction = (BeanElementUpdateMetadataProcess) proxy.getAction();
		  processAction.setValidationActiveUser("testuser");
		  processAction.setSelectedCommunity(new CommunityHandle(processAction.getLoggedUserProfile(),c));
		  processAction.getSelectedCommunity().updateFull();
		  processAction.getSelectedCommunity().getSelectedCatalog().setSelectedElement(1000);
		  result = proxy.execute();
		
		  assertEquals("Result returned form executing the action was not success but it should have been.", 
										BeanProcessResult.BeanProcess_SUCCESS.toString(), result);
		
		  assertEquals("The tested 'image' metadata has not been set as Thumbnail as expected",true,
				  			processAction.getSelectedCommunity().getSelectedCatalog(). 
				  				getSelectedElement().getDataset(1000).getMetadata(1000).getAsImage().isThumbnail());

		  
		  assertEquals("Element Thumbnail Url has not been updated as expected", 
				  metadataImageUrl,processAction.getSelectedCommunity().getSelectedCatalog().getSelectedElement().getThumbnailUrl());
			   

	  }
	
		
	  /**
	   * Edit existing metadata 
	   */
	  public void testDeleteMetadata() throws Exception {
		  
		  String communityIdName="Test Community";
		  
		  int elementId=1000;
		  int metadataId=1666;
		  int datasetId=1000;
		  
		  // 1- Delete required metadata
		  request.setParameter("formMetadataId", (new Integer(metadataId)).toString());
		  
		  CommunitiesAccessor.reset();
	      ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		    
		  ActionProxy proxy = getActionProxy("/deleteMetadataProcess");
	 
	        BeanElementDeleteMetadataProcess processAction = (BeanElementDeleteMetadataProcess) proxy.getAction();
	        processAction.setValidationActiveUser("testuser");
	        processAction.setSelectedCommunity(new CommunityHandle(processAction.getLoggedUserProfile(),c));
	        processAction.getSelectedCommunity().updateFull();
     
	        processAction.getSelectedCommunity().getSelectedCatalog().setSelectedElement(elementId);
	      	        
	        assertNotNull("Metadata '"+metadataId+"' not found in dataset '"+datasetId+"' before deleting it", 
	        		processAction.getSelectedCommunity().getSelectedCatalog().getSelectedElement().getDataset(datasetId).getMetadata(metadataId));
	        
	        String result = proxy.execute();
	        
	        assertEquals("Result returned form executing the action was not success but it should have been.", 
	        								BeanProcessResult.BeanProcess_SUCCESS.toString(), result);
	        
	        boolean stillFound=true;
	        try {
	        	processAction.getSelectedCommunity().getSelectedCatalog().getSelectedElement().getDataset(datasetId).getMetadata(metadataId);
	        } catch (DataAccessErrorException e) { stillFound=false; }
	        
	        assertFalse("Deleted metadata '"+metadataId+"' still found in dataset '"+datasetId+"'.",stillFound);
	        
	  }	  

	  /**
	   * Create a metadata overriding template one 
	   */
	  public void testCreateMetadataFromTemplateWithImplicitNewDataset() throws Exception {
		  
		  String communityIdName="Test Community";
		  String newMetadataName="template metadata 3";
		  String newMetadataComment="A comment for this test metadata";
		  Integer datasetId=1011; 
		  Integer elementId=1020;
		  Integer termId=4;
		  
		  String implicitDatasetName="Another template dataset";
		  
		  // 1- Create a new metadata
		  request.setParameter("formMetadataName", newMetadataName);
		  request.setParameter("formMetadataComment", newMetadataComment);
		  request.setParameter("formDatasetId", datasetId.toString());
		  request.setParameter("formTermId", termId.toString());
		  
		  CommunitiesAccessor.reset();
	      ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		    
		  ActionProxy proxy = getActionProxy("/addMetadataProcess");
	 
		  	
	        BeanElementAddMetadataProcess processAction = (BeanElementAddMetadataProcess) proxy.getAction();
	        processAction.setValidationActiveUser("testuser");
	        processAction.setSelectedCommunity(new CommunityHandle(processAction.getLoggedUserProfile(),c));
	        processAction.getSelectedCommunity().updateFull();
   	        processAction.getSelectedCatalog().setSelectedElement(elementId);
   	        	   	    
   	        processAction.getSelectedElement().updateFull();
	         
	        assertEquals("Required dataset is not found while it should be there at this step", 
	        		(int)processAction.getSelectedElement().getDatasets().get(1).getDatasetId(),(int)datasetId);
	        
	        IElementHandle el = processAction.getSelectedElement();
	        
	        Integer originalDatasetId=0;
	        Iterator<IDatasetHandle> it = el.getDatasets().iterator();
	        while (it.hasNext()) {
	        	IDatasetHandle curD = it.next();
	        	if (curD.getName().equals(implicitDatasetName)){
	        		originalDatasetId=curD.getDatasetId();
	        	}
	        }
	        
	        //int nbDatasetsBefore = el.getDatasets().size(); 
	        String result = proxy.execute();	        
	        el = processAction.getSelectedElement();
	        assertEquals("Result returned form executing the action was not success but it should have been.", 
	        								BeanProcessResult.BeanProcess_SUCCESS.toString(), result);
	        
	        assertNotSame("Created dataset should not have same datasetID than the original template one", 
	        											datasetId,processAction.getFormDatasetId());
	        
	        Integer implicitDatasetId=0;
	        it = el.getDatasets().iterator();
	        while (it.hasNext()) {
	        	IDatasetHandle curD = it.next();
	        	if (curD.getName().equals(implicitDatasetName)){
	        		implicitDatasetId=curD.getDatasetId();
	        	}
	        }
	        
	        assertNotSame("Implicit dataset does not seem to have been created in target element "
	        +"(concerned dataset still have original id from template element'"+originalDatasetId+"')", originalDatasetId, implicitDatasetId);
	        
	        assertNotSame("Previous template dataset is still in the element while it shouldn't at this step", 
					(int)processAction.getSelectedElement().getDatasets().get(1).getDatasetId(),(int)datasetId);
	        
	    }
	  
	  public void testMetadataConstraintsCheck() throws Exception {

		  String communityIdName="Test Community";
		  
		  // Currently selected element is set up later below.		  
		  String field="selectedElement.datasetsMap['1000'].metadatasMap['1000'].string1";
		  String value="TOO long Text blabablabla blabablabla blabablablablabablabla blabablabla blabablabla blabablablablabablabla"
				  +"blabablablablabablabla blabablablablabablabla blabablablablabablabla blabablablablabablabla blabablablablabablabla"
				  +"blabablablablabablabla blabablablablabablabla blabablablablabablabla blabablablablabablabla blabablablablabablabla"
				  +"blabablablablabablabla blabablablablabablabla blabablablablabablabla blabablablablabablabla blabablablablabablabla"
				  +"blabablablablabablabla blabablablablabablabla blabablablablabablabla blabablablablabablabla blabablablablabablabla"
				  +"blabablablablabablabla blabablablablabablabla blabablablablabablabla blabablablablabablabla blabablablablabablabla"
				  +"blabablablablabablabla blabablablablabablabla blabablablablabablabla blabablablablabablabla blabablablablabablabla"
				  ;
		 
		  
		  // 1- Create a new metadata
		  request.setParameter(field, value);
		  
		  CommunitiesAccessor.reset();
	      ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		    
		  ActionProxy proxy = getActionProxy("/updateMetadataProcess");
	 
	        BeanElementUpdateMetadataProcess processAction = (BeanElementUpdateMetadataProcess) proxy.getAction();
	        processAction.setValidationActiveUser("testuser");
	        processAction.setSelectedCommunity(new CommunityHandle(processAction.getLoggedUserProfile(),c));
	        processAction.getSelectedCommunity().updateFull();
     
	        processAction.getSelectedCommunity().getSelectedCatalog().setSelectedElement(1000);
	      	        
	        String previousString=c.getMetadata(1000).getString1();
	        
	        
	        String result = proxy.execute();
	        assertEquals("String length should have been raise an exception, but it didn't",
	        					BeanProcessResult.BeanProcess_DBERROR.toString(), result);
	        
	        
	        // reset DB state in order to not compromise next unit tests
	        c.getMetadata(1000).setString1(previousString);
	        c.getMetadata(1000).commit(processAction.getLoggedUserProfile());
	         
	  }
	  }
	  
