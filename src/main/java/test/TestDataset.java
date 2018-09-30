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
import metaindex.data.dataset.Dataset;
import metaindex.data.dataset.IDataset;
import metaindex.data.dataset.beans.BeanElementAddDatasetProcess;
import metaindex.data.dataset.beans.BeanElementDeleteDatasetProcess;
import metaindex.data.element.Element;
import metaindex.data.element.IElement;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.beans.BeanElementAddMetadataProcess;
import metaindex.data.metadata.beans.BeanElementDeleteMetadataProcess;
import metaindex.data.metadata.beans.BeanElementMoveMetadataProcess;
import metaindex.data.userprofile.GuiLanguageData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;

import com.opensymphony.xwork2.ActionProxy;

public class TestDataset extends StrutsSpringTestCase {

	private static Log log = LogFactory.getLog(TestCommunity.class);
	//private DBUsersAccessor userprofileAccessor;
	
	  /**
	   * Create a good new Test Community 
	   */
	  public void testCreateDataset() throws Exception {
		  
		  String communityIdName="Test Community";
		  String newDatasetName="A new test dataset";
		  Integer elementId=1000; // we know that our test database is built with a test element having its id=1000
		   
		  
		  // 1- Create a new metadata
		  request.setParameter("formDatasetName", newDatasetName);
		  request.setParameter("formDatasetElementId", elementId.toString());
		  
		  ActionProxy proxy = getActionProxy("/addDatasetProcess");
	 
		  CommunitiesAccessor.reset();
	      ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		  
	        BeanElementAddDatasetProcess processAction = (BeanElementAddDatasetProcess) proxy.getAction();
	        processAction.setValidationActiveUser("testuser");
	        processAction.setSelectedCommunity(new CommunityHandle(processAction.getLoggedUserProfile(),c));
	        processAction.getSelectedCommunity().updateFull();
     
	        processAction.getSelectedCommunity().getSelectedCatalog().setSelectedElement(elementId);
	       
	        assertEquals("Nb of found datasets (before adding one) was not as expected.", 
					2, processAction.getSelectedCommunity().getSelectedCatalog().getSelectedElement().getDatasets().size());
	        
	        String result = proxy.execute();
	        
	        assertEquals("Result returned form executing the action was not success but it should have been.", 
	        								BeanProcessResult.BeanProcess_SUCCESS.toString(), result);
	        
	        assertEquals("Nb of found datasets (after adding one) was not as expected.", 
					3, processAction.getSelectedCommunity().getSelectedCatalog().getSelectedElement().getDatasets().size());
	        
	        // delete created dataset in order to not poluate next UTests
	        List<IDataset> ld = c.getElement(elementId).getDatasets();
	        Iterator<IDataset> it = ld.iterator();
	        while (it.hasNext()) {
	        	IDataset cur = it.next();
	        	if (cur.getName().equals(newDatasetName)) {
	        		cur.delete(processAction.getLoggedUserProfile());
	        	}
	        }
	        
	    }
	  
	  /**
	   * Create a good new Test Community 
	   */
	  public void testMoveMetadata() throws Exception {
		  
		  int selectedElementId=1000; // we know that our test database is built with a test element having its id=1000
		  int targetUserDatasetId=1000;
		  String communityIdName="Test Community";
		  String movedMetadataId="1002";
		  String moveToDatasetId=new Integer(targetUserDatasetId).toString();
		  String moveToColumn="3";
		  String moveToPosition="3";

		  
		  // 1- Create a new metadata
		  request.setParameter("movedMetadataId", movedMetadataId);
		  request.setParameter("moveToDatasetId", moveToDatasetId);
		  request.setParameter("moveToColumn", moveToColumn);
		  request.setParameter("moveToPosition", moveToPosition);
		  request.setParameter("moveToDatasetId", moveToDatasetId);
		  
		  ActionProxy proxy = getActionProxy("/moveMetadataProcess");
		  CommunitiesAccessor.reset();
	      ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		  
		  	BeanElementMoveMetadataProcess processAction = (BeanElementMoveMetadataProcess) proxy.getAction();
		  	processAction.setValidationActiveUser("testuser");
		  	processAction.setSelectedCommunity(new CommunityHandle(processAction.getLoggedUserProfile(),c));
	        processAction.getSelectedCommunity().updateFull();
     
	        processAction.getSelectedCommunity().getSelectedCatalog().setSelectedElement(selectedElementId);
	       
		  	// List start from 0, so we -1 the column nb
		  	assertEquals("Nb of found metadata in column 2 of selectedElement before performing Metadata move operation", 
					1, processAction.getSelectedCommunity().getSelectedCatalog().getSelectedElement()
														.getDataset(targetUserDatasetId).getColumnsMetadata().get(1).size());
		 // List start from 0, so we -1 the column nb
	        assertEquals("Nb of found metadata in column 3 of selectedElement before performing Metadata move operation", 
					2, processAction.getSelectedCommunity().getSelectedCatalog().getSelectedElement()
														.getDataset(targetUserDatasetId).getColumnsMetadata().get(2).size());
	        
	        assertEquals("Check that the first element of column 3 before insert (moving) another one is position 2", 
					2, processAction.getSelectedCommunity().getSelectedCatalog().getSelectedElement()
														.getDataset(targetUserDatasetId).getMetadata(1003).getLayoutPosition());
	        assertEquals("Check that the last element of column 3 before insert (moving) another one is position 3", 
					3, processAction.getSelectedCommunity().getSelectedCatalog().getSelectedElement()
														.getDataset(targetUserDatasetId).getMetadata(1004).getLayoutPosition());
	        
	        String result = proxy.execute();
	        
	        assertEquals("Result returned form executing the action was not success but it should have been.", 
	        								BeanProcessResult.BeanProcess_SUCCESS.toString(), result);
	     // List start from 0, so we -1 the column nb
		  	assertEquals("Nb of found metadata in column 2 of selectedElement after performing Metadata move operation", 
					0, processAction.getSelectedCommunity().getSelectedCatalog().getSelectedElement()
														.getDataset(targetUserDatasetId).getColumnsMetadata().get(1).size());
		 // List start from 0, so we -1 the column nb
	        assertEquals("Nb of found metadata in column 3 of selectedElement after performing Metadata move operation", 
					3, processAction.getSelectedCommunity().getSelectedCatalog().getSelectedElement()
														.getDataset(targetUserDatasetId).getColumnsMetadata().get(2).size());
	        
	        assertEquals("Check that the first element of column 3 after insert (moving) another one is still position 2", 
					2, processAction.getSelectedCommunity().getSelectedCatalog().getSelectedElement()
														.getDataset(targetUserDatasetId).getMetadata(1003).getLayoutPosition());
	        
	        assertEquals("Check that the element inserted in col 3 (moved) is at position 3", 
					3, processAction.getSelectedCommunity().getSelectedCatalog().getSelectedElement()
														.getDataset(targetUserDatasetId).getMetadata(1002).getLayoutPosition());
	        assertEquals("Check that the last element of column 3 after insert (moving) another one changed to position 4", 
					4, processAction.getSelectedCommunity().getSelectedCatalog().getSelectedElement()
														.getDataset(targetUserDatasetId).getMetadata(1004).getLayoutPosition());
	    }
	  
		
	  /**
	   * Edit existing metadata 
	   */
	  public void testDeleteDataset() throws Exception {
		  
		  String communityIdName="Test Community";
		  
		  int elementId=1000;
		  Integer datasetId=1666;
		  
		  // 1- Delete required dataset
		  request.setParameter("formDatasetId", datasetId.toString());
		  
		  ActionProxy proxy = getActionProxy("/deleteDatasetProcess");
		  CommunitiesAccessor.reset();
	      ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		  
	        BeanElementDeleteDatasetProcess processAction = (BeanElementDeleteDatasetProcess) proxy.getAction();
	        processAction.setValidationActiveUser("testuser");
	        processAction.setSelectedCommunity(new CommunityHandle(processAction.getLoggedUserProfile(),c));
	        processAction.getSelectedCommunity().updateFull();
     
	        processAction.getSelectedCommunity().getSelectedCatalog().setSelectedElement(elementId);
	      	        
	        assertNotNull("Dataset '"+datasetId+"' not found in element '"+elementId+"' before deleting it", 
	        		processAction.getSelectedCommunity().getSelectedCatalog().getSelectedElement().getDataset(datasetId));
	        
	        String result = proxy.execute();
	        
	        assertEquals("Result returned form executing the action was not success but it should have been.", 
	        								BeanProcessResult.BeanProcess_SUCCESS.toString(), result);
	        
	        boolean stillFound=true;
	        try {
	        	processAction.getSelectedCommunity().getSelectedCatalog().getSelectedElement().getDataset(datasetId);
	        } catch (DataAccessErrorException e) { stillFound=false; }
	        
	        assertFalse("Dataset '"+datasetId+"' still found in element '"+elementId+"' after deleting it", stillFound);
	        	        
	        // TODO check that related metadata has also been deleted
	  }	 

}
