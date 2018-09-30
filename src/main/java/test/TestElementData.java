package metaindex.test;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.StrutsSpringTestCase;

import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityHandle;
import metaindex.data.community.ICommunity;
import metaindex.data.community.beans.BeanCommunityNextElementDataProcess;
import metaindex.data.dataset.beans.BeanElementDeleteDatasetProcess;
import metaindex.data.element.Element;
import metaindex.data.element.ElementHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.element.beans.BeanElementAddElementDataProcess;
import metaindex.data.element.beans.BeanElementDeleteElementDataProcess;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;

import com.opensymphony.xwork2.ActionProxy;

public class TestElementData extends StrutsSpringTestCase {

	private static Log log = LogFactory.getLog(TestCommunity.class);
	//private DBUsersAccessor userprofileAccessor;
	
	  /**
	   * Create a good new Test Community 
	   */
	  public void testCreateElementData() throws Exception {
		  
		  String communityIdName="Test Community";
		  String newElementName="A new test element";
		  String newElementComment="A comment for this element";
		  String[] newElementCatalogs = { "1001","1000" };
		  
		  // 1- Create a new metadata
		  request.setParameter("newElementName", newElementName);
		  request.setParameter("newElementComment", newElementComment);
		  request.setParameter("newElementCatalogs", newElementCatalogs);
		  
		  ActionProxy proxy = getActionProxy("/addElementDataProcess");
		  CommunitiesAccessor.reset();
	      ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		  
	        BeanElementAddElementDataProcess processAction = (BeanElementAddElementDataProcess) proxy.getAction();
	        processAction.setValidationActiveUser("testuser");
	        processAction.setSelectedCommunity(new CommunityHandle(processAction.getLoggedUserProfile(),c));
	        processAction.getSelectedCommunity().updateFull();
     	        
	        assertEquals("Nb of found elements (before adding one) was not as expected.", 
					6, processAction.getSelectedCommunity().getElements().size());
	        
	        
	        assertEquals("Nb of found elements (before adding one) statically referenced by catalog '"
	        		+processAction.getSelectedCommunity().getCatalog(1001).getName()+"' was not as expected.", 
	        		1, processAction.getSelectedCommunity().getCatalog(1001).getNbStaticElements());
	        
	        assertEquals("Nb of found elements (before adding one) dynamically referenced by catalog '"
	        		+processAction.getSelectedCommunity().getCatalog(1001).getName()+"' was not as expected.", 
	        		0, processAction.getSelectedCommunity().getCatalog(1001).getNbDynamicElements());
	        
	        String result = proxy.execute();
	        
	        assertEquals("Result returned form executing the action was not success but it should have been.", 
	        								BeanProcessResult.BeanProcess_SUCCESS.toString(), result);
	        
	        assertEquals("Nb of found elements (after adding one) was not as expected.", 
	        		7, processAction.getSelectedCommunity().getElements().size());
	        
	    
	        assertEquals("Nb of found elements statically referenced by catalog '"
	        		+processAction.getSelectedCommunity().getCatalog(1001).getName()+"' was not as expected.", 
	        		2, processAction.getSelectedCommunity().getCatalog(1001).getNbStaticElements());
	        
	        assertEquals("Nb of found elements (before adding one) dynamically referenced by catalog '"
	        		+processAction.getSelectedCommunity().getCatalog(1001).getName()+"' was not as expected.", 
	        		0, processAction.getSelectedCommunity().getCatalog(1001).getNbDynamicElements());
	    }
	
	  
		
	  /**
	   * Edit existing metadata 
	   */
	  public void testDeleteElement() throws Exception {
		  
		  String communityIdName="Test Community";
		  
		  int elementId=1666;		  
		  
		  // 1- Delete required element
		  request.setParameter("formElementId", (new Integer(elementId)).toString());
		  
		  ActionProxy proxy = getActionProxy("/deleteElementDataProcess");
		  CommunitiesAccessor.reset();
	      ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		  
	        BeanElementDeleteElementDataProcess processAction = (BeanElementDeleteElementDataProcess) proxy.getAction();
	        processAction.setValidationActiveUser("testuser");
	        processAction.setSelectedCommunity(new CommunityHandle(processAction.getLoggedUserProfile(),c));
	        processAction.getSelectedCommunity().updateFull();
     
	        processAction.getSelectedCommunity().getSelectedCatalog().setSelectedElement(elementId);
	      	        
	        assertNotNull("Before deleting it, element '"+elementId+"' not found in community", 
	        		processAction.getSelectedCommunity().getElement(elementId));
	        
	        assertNotNull("Before deleting it, element '"+elementId+"' not found in current catalog", 
	        		processAction.getSelectedCommunity().getSelectedCatalog().getElement(elementId));
	        
	        processAction.getSelectedCommunity().getElement(elementId).updateFull();
	        String result = proxy.execute();
	        
	        assertEquals("Result returned form executing the action was not success but it should have been.", 
	        								BeanProcessResult.BeanProcess_SUCCESS.toString(), result);
	        	        
	        boolean stillFound=true;
	        try {
	        	processAction.getSelectedCommunity().getSelectedCatalog().getElement(elementId);
	        } catch (DataAccessErrorException e) { stillFound=false; }
	        
	        assertFalse("Element '"+elementId+"' still found after deleting it", stillFound); 
	        		
	        
	        // TODO maybe check that related metadata and dataset have also been deleted ...
	  }	 
	  

	  /**
	   * Create a good new Test Community 
	   */
	  public void testTemplateElement() throws Exception {
		  
		  // Element 1010 is template ref
		  // Element 1020 is an instance of template element 1010
		  String communityIdName="Test Community";
		  Integer refElementId=1010;
		  Integer instanceElementId=1020;
		  
		  
		  ActionProxy proxy = getActionProxy("/addElementDataProcess");
		  CommunitiesAccessor.reset();
	      ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		  
	        BeanElementAddElementDataProcess processAction = (BeanElementAddElementDataProcess) proxy.getAction();
	        processAction.setValidationActiveUser("testuser");
	        processAction.setSelectedCommunity(new CommunityHandle(processAction.getLoggedUserProfile(),c));	        
	        processAction.getSelectedCommunity().updateFull();
	        
	        IElementHandle ref = processAction.getSelectedCommunity().getElement(refElementId);
	        IElementHandle instance = processAction.getSelectedCommunity().getElement(instanceElementId);
	        ref.updateFull();
	        instance.updateFull();
	        
	        assertTrue(instance.isTemplated());
	        assertTrue(ref.isTemplate());
	        assertFalse(instance.isTemplate());
	        assertTrue(instance.isModifyOverridenTemplate());
	        assertTrue(instance.getDatasets().get(0).isTemplated());
	        assertTrue(instance.getDatasets().get(0).isModifyOverridenTemplate());
	        assertTrue(instance.getDatasets().get(1).isTemplated());
	        assertFalse(instance.getDatasets().get(1).isModifyOverridenTemplate());
	        // Dataset 0 Metadata 0 : value is overriden 
	        assertTrue(instance.getDatasets().get(0).getMetadata().get(0).isTemplated());
	        assertTrue(instance.getDatasets().get(0).getMetadata().get(0).isModifyOverridenTemplate());
	        assertFalse(instance.getDatasets().get(0).getMetadata().get(0).isReadOnly());
	        // Dataset 0 Metadata 1 : value is not overriden : the template version is used
	        assertTrue(instance.getDatasets().get(0).getMetadata().get(1).isTemplated());
	        assertFalse(instance.getDatasets().get(0).getMetadata().get(1).isModifyOverridenTemplate());
	        assertTrue(instance.getDatasets().get(0).getMetadata().get(1).isReadOnly());
	        
	        // Dataset 1 Metadata 0 : value is overriden
	        assertTrue(instance.getDatasets().get(1).getMetadata().get(0).isTemplated());
	        assertFalse(instance.getDatasets().get(1).getMetadata().get(0).isModifyOverridenTemplate());
	        assertTrue(instance.getDatasets().get(1).getMetadata().get(0).isReadOnly());
	        
	        // check that structure and layout is compliant to the ref
	        assertEquals("Nb of datasets not compliant to ref. template", 
					ref.getNbDatasets(), instance.getDatasets().size());

	        assertEquals("Nb columns does not match the ref. template", 
					ref.getDatasets().get(0).getLayoutNbColumns(), instance.getDatasets().get(0).getLayoutNbColumns());
	        assertEquals("Nb columns does not match the ref. template", 
					ref.getDatasets().get(1).getLayoutNbColumns(), instance.getDatasets().get(1).getLayoutNbColumns());
	        
	        // check datasets names
	        assertEquals("Name of datasets should match with the ref. template.", 
					ref.getDatasets().get(0).getName(), instance.getDatasets().get(0).getName());
	        assertEquals("Name of datasets should match with the ref. template.", 
					ref.getDatasets().get(1).getName(), instance.getDatasets().get(1).getName());
	        
	        // check metadatas structure
	        assertEquals("Nb of metadata not compliant to ref. template", 
					ref.getDatasets().get(0).getNbMetadata(), instance.getDatasets().get(0).getNbMetadata());
	        assertEquals("Nb of metadata not compliant to ref. template", 
					ref.getDatasets().get(1).getNbMetadata(), instance.getDatasets().get(1).getNbMetadata());
	        
	        // check metadata names 
	        assertEquals("Name of metadata should match with the ref. template.", 
					ref.getDatasets().get(0).getMetadata().get(0).getName(),
					instance.getDatasets().get(0).getMetadata().get(0).getName());
	        assertEquals("Name of metadata should match with the ref. template.", 
					ref.getDatasets().get(0).getMetadata().get(1).getName(),
					instance.getDatasets().get(0).getMetadata().get(1).getName());
	        assertEquals("Name of metadata should match with the ref. template.", 
					ref.getDatasets().get(1).getMetadata().get(0).getName(),
					instance.getDatasets().get(1).getMetadata().get(0).getName());
	        
	        // checking Metadata 1020 should be overridden	        
	        assertFalse("Value of metadata should not match with the ref. template.", 
					ref.getDatasets().get(0).getMetadata().get(0).getValueNumber1()==
					instance.getDatasets().get(0).getMetadata().get(0).getValueNumber1());
	  
	        // checking Metadata 1021 does not override 1011 since the name does not match
	        // in the 1021 object.
	        assertTrue("Value of metadata should match with the ref. template.", 
					ref.getDatasets().get(0).getMetadata().get(1).getValueNumber1()==
					instance.getDatasets().get(0).getMetadata().get(1).getValueNumber1());
	        
	        // checking searchText contents
	        /*
	        log.error("### Element SearchText= "+instance.getSearchText());
	        log.error("### Ref.    SearchText= 1020 - Template Instance;Test template instance element;"
	        					+"A template dataset;template ref version;"
	        						+"template metadata 1;; text 4;;"
	        						+"template metadata 2;; text 2;;;"
        						+"Another template dataset;template ref version;"
	        						+"template metadata 3;; text 3;;");
	        						*/
	        assertEquals("SearchText for element '"+instance.getElementId()+"' is not as expected",
	        		"1020 - Template Instance;Test template instance element;"
	        					+"A template dataset;template ref version;"
	        						+"template metadata 1;; text 4;;"
	        						+"template metadata 2;; text 2;;;"
        						+"Another template dataset;template ref version;"
	        						+"template metadata 3;; text 3;;"
	        		,instance.getSearchText());
	        
	        
	        
	    }
	
	  
	  
}
