package metaindex.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.StrutsSpringTestCase;

import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.CatalogHandle;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogContents;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.catalog.TemplatesElementsCatalog;
import metaindex.data.catalog.beans.BeanAddStaticElementToCatalogProcess;
import metaindex.data.catalog.beans.BeanCommunityAddCatalogProcess;
import metaindex.data.catalog.beans.BeanCommunityDownloadCatalog;
import metaindex.data.catalog.beans.BeanCommunityUpdateCatalogProcess;
import metaindex.data.catalog.beans.BeanCommunityUploadCSVCatalog;
import metaindex.data.catalog.beans.BeanCommunitySynchronizeOMCCatalog;
import metaindex.data.catalog.beans.BeanRemoveStaticElementFromCatalogProcess;
import metaindex.data.catalog.beans.BeanSelectCatalogProcess;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityHandle;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunityTerm;
import metaindex.data.community.beans.BeanCreateCommunityProcess;
import metaindex.data.community.beans.BeanUpdateCommunityProcess;
import metaindex.data.dataset.beans.BeanElementAddDatasetProcess;
import metaindex.data.element.Element;
import metaindex.data.element.ElementHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.beans.BeanElementAddMetadataProcess;
import metaindex.data.userprofile.GuiLanguageData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IGenericEncodable.POPULATE_POLICY;

import com.opensymphony.xwork2.ActionProxy;

public class TestCatalog extends StrutsSpringTestCase {

	private static Log log = LogFactory.getLog(TestCommunity.class);
	
	private static final String expectedCatalogContents="{\"terms\":{\"3\":{\"idName\":\"Title\",\"datatypeId\":5},\"4\":{\"idName\":\"Author\",\"datatypeId\":5},\"5\":{\"idName\":\"Illustration\",\"datatypeId\":5},\"666\":{\"idName\":\"term to be deleted\",\"datatypeId\":5}},\"metadatas\":{\"1666\":{\"layoutPosition\":1,\"valueNumber4\":0,\"valueNumber2\":0,\"layoutAlign\":\"center\",\"valueNumber3\":0,\"longString\":\"\",\"valueNumber1\":0,\"metadataId\":1666,\"termId\":4,\"string3\":\"\",\"string4\":\"\",\"string1\":\"\",\"layoutSize\":\"normal\",\"string2\":\"\",\"name\":\"Metadata to be deleted\",\"datasetId\":1000,\"layoutDoDisplayName\":true,\"comment\":\"\",\"layoutColumn\":1},\"1004\":{\"layoutPosition\":3,\"valueNumber4\":0,\"valueNumber2\":0,\"layoutAlign\":\"center\",\"valueNumber3\":0,\"longString\":\"\",\"valueNumber1\":0,\"metadataId\":1004,\"termId\":4,\"string3\":\"\",\"string4\":\"\",\"string1\":\"\",\"layoutSize\":\"normal\",\"string2\":\"\",\"name\":\"in the 3rd col too\",\"datasetId\":1000,\"layoutDoDisplayName\":true,\"comment\":\"\",\"layoutColumn\":3},\"1003\":{\"layoutPosition\":2,\"valueNumber4\":0,\"valueNumber2\":0,\"layoutAlign\":\"center\",\"valueNumber3\":0,\"longString\":\"\",\"valueNumber1\":0,\"metadataId\":1003,\"termId\":4,\"string3\":\"\",\"string4\":\"\",\"string1\":\"\",\"layoutSize\":\"normal\",\"string2\":\"\",\"name\":\"in the 3rd col\",\"datasetId\":1000,\"layoutDoDisplayName\":true,\"comment\":\"\",\"layoutColumn\":3},\"1002\":{\"layoutPosition\":4,\"valueNumber4\":0,\"valueNumber2\":0,\"layoutAlign\":\"center\",\"valueNumber3\":0,\"longString\":\"\",\"valueNumber1\":0,\"metadataId\":1002,\"termId\":4,\"string3\":\"\",\"string4\":\"\",\"string1\":\"\",\"layoutSize\":\"normal\",\"string2\":\"\",\"name\":\"second column\",\"datasetId\":1000,\"layoutDoDisplayName\":true,\"comment\":\"\",\"layoutColumn\":2},\"1001\":{\"layoutPosition\":1,\"valueNumber4\":0,\"valueNumber2\":0,\"layoutAlign\":\"center\",\"valueNumber3\":0,\"longString\":\"\",\"valueNumber1\":0,\"metadataId\":1001,\"termId\":4,\"string3\":\"\",\"string4\":\"\",\"string1\":\"\",\"layoutSize\":\"normal\",\"string2\":\"\",\"name\":\"Second test metadata\",\"datasetId\":1000,\"layoutDoDisplayName\":true,\"comment\":\"\",\"layoutColumn\":1},\"1000\":{\"layoutPosition\":1,\"valueNumber4\":0,\"valueNumber2\":0,\"layoutAlign\":\"center\",\"valueNumber3\":0,\"longString\":\"\",\"valueNumber1\":0,\"metadataId\":1000,\"termId\":4,\"string3\":\"\",\"string4\":\"\",\"string1\":\"\",\"layoutSize\":\"normal\",\"string2\":\"\",\"name\":\"First test metadata\",\"datasetId\":1000,\"layoutDoDisplayName\":true,\"comment\":\"\",\"layoutColumn\":1}},\"elements\":{\"1000\":{\"elementId\":1000,\"template\":false,\"name\":\"A test element\",\"templateRefElementId\":0,\"comment\":\"This element shall be dynamically referenced by the test catalog\",\"thumbnailUrl\":\"\"}},\"datasets\":{\"1666\":{\"elementId\":1000,\"layoutNbColumns\":3,\"layoutPosition\":1,\"name\":\"A test dataset to be deleted\",\"datasetId\":1666,\"layoutAlwaysExpand\":false,\"layoutDoDisplayName\":true,\"comment\":\"\"},\"1001\":{\"elementId\":1000,\"layoutNbColumns\":3,\"layoutPosition\":1,\"name\":\"Another test dataset\",\"datasetId\":1001,\"layoutAlwaysExpand\":false,\"layoutDoDisplayName\":true,\"comment\":\"\"},\"1000\":{\"elementId\":1000,\"layoutNbColumns\":3,\"layoutPosition\":1,\"name\":\"A test dataset\",\"datasetId\":1000,\"layoutAlwaysExpand\":false,\"layoutDoDisplayName\":true,\"comment\":\"\"}}}";		
	private static final String expectedCatalogContentsDifferentTermsIds="{\"terms\":{\"42\":{\"idName\":\"DummyNew\",\"datatypeId\":6},\"33\":{\"idName\":\"Title\",\"datatypeId\":5},\"44\":{\"idName\":\"Author\",\"datatypeId\":5},\"123\":{\"idName\":\"term to be deleted\",\"datatypeId\":5}},\"metadatas\":{\"1666\":{\"layoutPosition\":1,\"valueNumber4\":0,\"valueNumber2\":0,\"layoutAlign\":\"center\",\"valueNumber3\":0,\"longString\":\"\",\"valueNumber1\":0,\"termId\":42,\"metadataId\":1666,\"string3\":\"\",\"string4\":\"\",\"string1\":\"\",\"layoutSize\":\"normal\",\"string2\":\"\",\"name\":\"Metadata to be deleted\",\"datasetId\":1000,\"layoutDoDisplayName\":true,\"comment\":\"\",\"layoutColumn\":1},\"1004\":{\"layoutPosition\":3,\"valueNumber4\":0,\"valueNumber2\":0,\"layoutAlign\":\"center\",\"valueNumber3\":0,\"longString\":\"\",\"valueNumber1\":0,\"termId\":44,\"metadataId\":1004,\"string3\":\"\",\"string4\":\"\",\"string1\":\"\",\"layoutSize\":\"normal\",\"string2\":\"\",\"name\":\"in the 3rd col too\",\"datasetId\":1000,\"layoutDoDisplayName\":true,\"comment\":\"\",\"layoutColumn\":3},\"1003\":{\"layoutPosition\":2,\"valueNumber4\":0,\"valueNumber2\":0,\"layoutAlign\":\"center\",\"valueNumber3\":0,\"longString\":\"\",\"valueNumber1\":0,\"termId\":44,\"metadataId\":1003,\"string3\":\"\",\"string4\":\"\",\"string1\":\"\",\"layoutSize\":\"normal\",\"string2\":\"\",\"name\":\"in the 3rd col\",\"datasetId\":1000,\"layoutDoDisplayName\":true,\"comment\":\"\",\"layoutColumn\":3},\"1002\":{\"layoutPosition\":4,\"valueNumber4\":0,\"valueNumber2\":0,\"layoutAlign\":\"center\",\"valueNumber3\":0,\"longString\":\"\",\"valueNumber1\":0,\"termId\":44,\"metadataId\":1002,\"string3\":\"\",\"string4\":\"\",\"string1\":\"\",\"layoutSize\":\"normal\",\"string2\":\"\",\"name\":\"second column\",\"datasetId\":1000,\"layoutDoDisplayName\":true,\"comment\":\"\",\"layoutColumn\":2},\"1001\":{\"layoutPosition\":1,\"valueNumber4\":0,\"valueNumber2\":0,\"layoutAlign\":\"center\",\"valueNumber3\":0,\"longString\":\"\",\"valueNumber1\":0,\"termId\":44,\"metadataId\":1001,\"string3\":\"\",\"string4\":\"\",\"string1\":\"\",\"layoutSize\":\"normal\",\"string2\":\"\",\"name\":\"Second test metadata\",\"datasetId\":1000,\"layoutDoDisplayName\":true,\"comment\":\"\",\"layoutColumn\":1},\"1000\":{\"layoutPosition\":1,\"valueNumber4\":0,\"valueNumber2\":0,\"layoutAlign\":\"center\",\"valueNumber3\":0,\"longString\":\"\",\"valueNumber1\":0,\"termId\":44,\"metadataId\":1000,\"string3\":\"\",\"string4\":\"\",\"string1\":\"\",\"layoutSize\":\"normal\",\"string2\":\"\",\"name\":\"First test metadata\",\"datasetId\":1000,\"layoutDoDisplayName\":true,\"comment\":\"\",\"layoutColumn\":1}},\"elements\":{\"1000\":{\"template\":false,\"elementId\":1000,\"name\":\"ploc ploc\",\"templateRefElementId\":0,\"comment\":\"This is a stupidly imported element\"}},\"datasets\":{\"1666\":{\"elementId\":1000,\"layoutNbColumns\":3,\"layoutPosition\":1,\"name\":\"A test dataset to be deleted\",\"datasetId\":1666,\"layoutAlwaysExpand\":false,\"layoutDoDisplayName\":true,\"comment\":\"\"},\"1001\":{\"elementId\":1000,\"layoutNbColumns\":3,\"layoutPosition\":1,\"name\":\"Another test dataset\",\"datasetId\":1001,\"layoutAlwaysExpand\":false,\"layoutDoDisplayName\":true,\"comment\":\"\"},\"1000\":{\"elementId\":1000,\"layoutNbColumns\":3,\"layoutPosition\":1,\"name\":\"A test dataset\",\"datasetId\":1000,\"layoutAlwaysExpand\":false,\"layoutDoDisplayName\":true,\"comment\":\"\"}}}";
	
	  /**
	   * Create a good new Test Community 
	   */
	  public void testCreateCatalog() throws Exception {
		  
		  String communityIdName="Test Community";
		  String newCatalogName="A new test catalog";
		  
		  
		  // 1- Create a new metadata
		  request.setParameter("newCatalogName", newCatalogName);
		  
		  ActionProxy proxy = getActionProxy("/addCatalogProcess");
		  
		  BeanCommunityAddCatalogProcess action = (BeanCommunityAddCatalogProcess) proxy.getAction();
		  action.setValidationActiveUser("testuser");
		  CommunitiesAccessor.reset();
		  ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		  
		  action.setSelectedCommunity(new CommunityHandle(action.getLoggedUserProfile(),c));
		  action.getSelectedCommunity().updateFull();
	        
	        String result = proxy.execute();
	        
	        assertEquals("Result returned form executing the action was not success but it should have been.", 
	        								BeanProcessResult.BeanProcess_SUCCESS.toString(), result);
	        
	    }
	  /**
	   * Create a good new Test Community 
	   */
	  public void testSelectCatalog() throws Exception {
		  
		  String communityIdName="Test Community";
		  String selectedCatalogId="1001";
		  
		  
		  // 1- Create a new metadata
		  request.setParameter("formSelectedCatalogId", selectedCatalogId);
		  
		  ActionProxy proxy = getActionProxy("/selectCatalogProcess");
	 
		  BeanSelectCatalogProcess action = (BeanSelectCatalogProcess) proxy.getAction();
		  action.setValidationActiveUser("testuser");
		  CommunitiesAccessor.reset();
		  ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		  action.setSelectedCommunity(new CommunityHandle(action.getLoggedUserProfile(),c));
		  
		  action.getSelectedCommunity().updateFull();
		  action.getSelectedCommunity().setSelectedCatalog(
				  				action.getSelectedCommunity().getCatalog(1000));
		  
	        assertEquals("Initially selected catalog was not as expected", 
					1000,action.getSelectedCommunity().getSelectedCatalog().getCatalogId());
	        
	        String result = proxy.execute();
	        
	        assertEquals("Result returned from executing the action was not success but it should have been.", 
	        								BeanProcessResult.BeanProcess_SUCCESS.toString(), result);
	        
	        assertEquals("Selected catalog was not as expected", 
					1001,action.getSelectedCommunity().getSelectedCatalog().getCatalogId());	        	        
	    }
	    

	  /**
	   * Create a good new Test Community 
	   */
	  public void testUpdateCatalog() throws Exception {
		  
		  String communityIdName="Test Community";
		  String newCatalogName="Ohohoh blablabla replaced catalog name";
		  String newComment="Yep man";
		  String newFilter="lolo";
		  
		  // 1- Create a new metadata
		  request.setParameter("formCatalogName", newCatalogName);
		  request.setParameter("formCatalogComment", newComment);
		  request.setParameter("formCatalogSearchQuery", newFilter);
		  
		  ActionProxy proxy = getActionProxy("/updateCatalogProcess");
	 		  
		  BeanCommunityUpdateCatalogProcess action = (BeanCommunityUpdateCatalogProcess) proxy.getAction();
		  action.setValidationActiveUser("testuser");
		  CommunitiesAccessor.reset();
		  ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		  action.setSelectedCommunity(new CommunityHandle(action.getLoggedUserProfile(),c));
		  action.getSelectedCommunity().updateFull();
		  action.getSelectedCommunity().setSelectedCatalog(
				  action.getSelectedCommunity().getCatalog(1000)); 
		  		  		  
		  assertEquals("Catalog name before update not as expected", 
					"Test Catalog",  action.getSelectedCommunity().getSelectedCatalog().getName());
	        
	        String result = proxy.execute();
	        
	        assertEquals("Result returned form executing the action was not success but it should have been.", 
	        								BeanProcessResult.BeanProcess_SUCCESS.toString(), result);
	        
	        assertEquals("Catalog name after update not as expected", 
	        		newCatalogName,  action.getSelectedCommunity().getSelectedCatalog().getName());
	        
	    }


	  /**
	   * Create a good new Test Community 
	   */
	  public void testAddStaticElement() throws Exception {
		  
		  String communityIdName="Test Community";
		  Integer formElementToAddId = 1001;
		  Integer catalogId = 1001;
		  String[] catalogIds = { catalogId.toString() };
		  
		  		  
		  // 1- Create a new metadata
		  request.setParameter("formElementStaticCatalogs", catalogIds);
		  request.setParameter("formElementToAddId", formElementToAddId.toString());
		  	
		  
		  ActionProxy proxy = getActionProxy("/addStaticElementToCatalogProcess");
	 
		  BeanAddStaticElementToCatalogProcess action = 
				  						(BeanAddStaticElementToCatalogProcess) proxy.getAction();
		  action.setValidationActiveUser("testuser");
		  CommunitiesAccessor.reset();
		  ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		  action.setSelectedCommunity(new CommunityHandle(action.getLoggedUserProfile(),c));
		  action.getSelectedCommunity().updateFull();
		  action.getSelectedCommunity().setSelectedCatalog(
		  action.getSelectedCommunity().getCatalog(catalogId)); 
		  
		  ICatalogHandle catalog = action.getSelectedCommunity().getCatalog(catalogId);
		  IElementHandle el = action.getSelectedCommunity().getElement(formElementToAddId);
		  
		  boolean elementIsInCatalog=true;
		  try { catalog.getElement(el.getElementId()); } catch (DataAccessErrorException e) { elementIsInCatalog=false; }
		  
		  // check that added element is not found in catalog before executing the action
		  assertFalse("Element to add is already in the catalog, so unit test would be useless...", elementIsInCatalog);
		  
	      String result = proxy.execute();
	        
	      assertEquals("Result returned form executing the action was not success but it should have been.", 
	        											BeanProcessResult.BeanProcess_SUCCESS.toString(), result);
	      
	      // check that new element is now found
	      el = catalog.getElement(formElementToAddId);
	      
	      // and check that it is seen as a static element
	      assertTrue(catalog.isStaticElement(el.getElementId()));
	        
	    }
	  

	  /**
	   * Create a good new Test Community 
	   */
	  public void testRemoveStaticElement() throws Exception {
		  
		  String communityIdName="Test Community";
		  Integer formElementToRemoveId = 1000;
		  Integer catalogId = 1000;
		  		  
		  request.setParameter("formCatalogId", catalogId.toString());
		  request.setParameter("formElementToRemoveId", formElementToRemoveId.toString());
		  	  
		  ActionProxy proxy = getActionProxy("/removeStaticElementToCatalogProcess");
	 
		  
		  BeanRemoveStaticElementFromCatalogProcess action = (BeanRemoveStaticElementFromCatalogProcess) proxy.getAction();
		  action.setValidationActiveUser("testuser");
		  CommunitiesAccessor.reset();
		  ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		  action.setSelectedCommunity(new CommunityHandle(action.getLoggedUserProfile(),c));
		  action.getSelectedCommunity().updateFull();
		  action.getSelectedCommunity().setSelectedCatalog(
				  													action.getSelectedCommunity().getCatalog(catalogId)); 
		  
		  ICatalogHandle catalog = action.getSelectedCommunity().getCatalog(catalogId);		  
		  IElementHandle el = catalog.getElement(formElementToRemoveId);
		  
		  // and check that it is seen as a static element
	      assertTrue(catalog.isStaticElement(el.getElementId()));
	      
	      String result = proxy.execute();
	      
	      assertEquals("Result returned form executing the action was not success but it should have been.", 
	        											BeanProcessResult.BeanProcess_SUCCESS.toString(), result);
	        
	      // check that new element is now found
	      
	      
	      boolean elementIsInCatalog=true;
	      try { catalog.getElement(formElementToRemoveId); } catch (DataAccessErrorException e) { elementIsInCatalog=false; }
	      assertFalse("Element '"+el.getElementId()+"' was to be removed but is still in catalog '"+catalog.getCatalogId()+"'", elementIsInCatalog);
	        
	    }	
	  
	  /**
	   * Create a good new Test Community 
	   */
	  public void testLoadTemplatesCatalog() throws Exception {
		  
		  String communityIdName="Test Community";
		  		  
		  ActionProxy proxy = getActionProxy("/selectCatalogProcess");
	 
		  
		  BeanSelectCatalogProcess action = (BeanSelectCatalogProcess) proxy.getAction();
		  action.setValidationActiveUser("testuser");
		  CommunitiesAccessor.reset();
		  ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		  action.setSelectedCommunity(new CommunityHandle(action.getLoggedUserProfile(),c));
		  action.getSelectedCommunity().updateFull();
		  
		  assertEquals("Nb of expected template elements was not as expected", 
					1,action.getSelectedCommunity().getCatalog(TemplatesElementsCatalog.TEMPLATES_ELEMENTS_CATALOG_ID).getNbDynamicElements());
	        	        	        
	    }
	    
	  /**
	   * Download catalog contents 
	   */
	  public void testDownloadCatalog() throws Exception {
		  
		  String communityIdName="Test Community";
		  Integer selectedCatalogId=1000;
		  		  
		  ActionProxy proxy = getActionProxy("/downloadCatalogProcess");
	 
		  BeanCommunityDownloadCatalog action = (BeanCommunityDownloadCatalog) proxy.getAction();
		  action.setValidationActiveUser("testuser");
		  CommunitiesAccessor.reset();
		  ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		  action.setSelectedCommunity(new CommunityHandle(action.getLoggedUserProfile(),c));
		  action.getSelectedCommunity().updateFull();
		  action.getSelectedCommunity().setSelectedCatalog(
				  action.getSelectedCommunity().getCatalog(selectedCatalogId));
		  	
		  String fileContents = action.getFileStrContents();
	       
		  // order of the fields vary from one run to other
		  // we judge here that comparing string length is enough
		  //log.error("### Received string : "+fileContents);
	      assertEquals("Downloaded file contents does not match expected one", fileContents.length(),expectedCatalogContents.length());	        	        
	    }
	    
	  /**
	   * Synchronize catalog contents with Open Metaindex Catalog file (OMC) 
	   */
	  public void testSynchronizeCatalogAlwaysCreate() throws Exception {
		  
		  String communityIdName="Test Community";
		  Integer formCatalogId=0;
		  String formFileContentType="text/json";
		  String formFileName="testfilecatalog.moc";
		  
		  File myFile = File.createTempFile(formFileName,"");
		  List<String> myLines = new ArrayList<String>();
		  myLines.add(expectedCatalogContents);
		  FileUtils.writeLines(myFile, "UTF-8", myLines);
		  
		  POPULATE_POLICY policy = POPULATE_POLICY.ALWAYS_CREATE;
		  
		  request.setParameter("catalogId", formCatalogId.toString());
		  request.setParameter("myFileContentType", formFileContentType);		  
		  request.setParameter("myFileName", formFileName);		  
		  request.setParameter("populatePolicy", policy.toString());		  
		  		  
		  ActionProxy proxy = getActionProxy("/SynchronizeOMCCatalogProcess");
	 		  
		  BeanCommunitySynchronizeOMCCatalog action = (BeanCommunitySynchronizeOMCCatalog) proxy.getAction();
		  action.setFormFile(myFile);
		  action.setPopulatePolicy(policy);
		  
		  action.setValidationActiveUser("testuser");
		  CommunitiesAccessor.reset();
		  ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		  action.setSelectedCommunity(new CommunityHandle(action.getLoggedUserProfile(),c));
		  action.getSelectedCommunity().updateFull();
		  
		  Integer expectedNbElsBefore = 5;
		  assertEquals("Catalog contents before synchronize not as expected", expectedNbElsBefore,
				  (Integer)action.getSelectedCatalog().getElementsCount());
		  
		  List<IElement> existingElementsBefore = new ArrayList<IElement>();
		  Iterator<IElement> it = c.getElements().iterator();
		  while (it.hasNext()) { existingElementsBefore.add(it.next()); }
		  List<ICommunityTerm> existingTermsBefore = new ArrayList<ICommunityTerm>();
		  Iterator<ICommunityTerm> itTerm = c.getTerms().iterator();
		  while (itTerm.hasNext()) { existingTermsBefore.add(itTerm.next()); }
		  
		  String result = proxy.execute();	      
		  Integer expectedNbElsAfter = expectedNbElsBefore+1;
		  		  
		  assertEquals("Result returned form executing the action was not success but it should have been.", 
					BeanProcessResult.BeanProcess_SUCCESS.toString(), result);		  		  
		  
		  assertEquals("Catalog contents not as expected after synchronization", expectedNbElsAfter,
				  (Integer)action.getSelectedCatalog().getElementsCount());	 
		  
		  // Catalog import added new terms and elements which shall be removed from the community so that
		  // it does not corrupt data for next tests
		  Iterator<ICommunityTerm> itTerms = c.getTerms().iterator(); 
		  List<ICommunityTerm> termsToDelete = new ArrayList<ICommunityTerm>();
		  List<IElement> elsToDelete = new ArrayList<IElement>();
		  
		  Iterator<IElement> it2 = c.getElements().iterator();
		  while (it2.hasNext()) {
			  IElement el = it2.next();
			  if (!existingElementsBefore.contains(el)) { elsToDelete.add(el); }
		  }
		  Iterator<ICommunityTerm> itTerm2 = c.getTerms().iterator();
		  while (itTerm2.hasNext()) {
			  ICommunityTerm t = itTerm2.next();
			  if (!existingTermsBefore.contains(t)) { termsToDelete.add(t); }
		  }
		  Iterator<IElement> itEls = elsToDelete.iterator();
		  itTerms = termsToDelete.iterator();
		  while (itEls.hasNext()) {
			  IElement curEl = itEls.next();
			  curEl.updateFull(action.getLoggedUserProfile());
			  curEl.delete(action.getLoggedUserProfile());
		  }
		  while (itTerms.hasNext()) { itTerms.next().delete(action.getLoggedUserProfile()); }
		  c.updateFull(action.getLoggedUserProfile());
	  	    
}
	  
	  /**
	   * Synchronize catalog contents 
	   */
	  public void testSynchronizeCatalogWithDifferentTermIds() throws Exception {
		  
		  String communityIdName="Test Community";
		  Integer formCatalogId=0;
		  String formFileContentType="text/json";
		  String formFileName="testfilecatalog.moc";
		  
		  File myFile = File.createTempFile(formFileName,"");
		  List<String> myLines = new ArrayList<String>();
		  myLines.add(expectedCatalogContentsDifferentTermsIds);
		  FileUtils.writeLines(myFile, "UTF-8", myLines);
		  
		  POPULATE_POLICY policy = POPULATE_POLICY.ALWAYS_CREATE;
		  
		  request.setParameter("catalogId", formCatalogId.toString());
		  request.setParameter("myFileContentType", formFileContentType);		  
		  request.setParameter("myFileName", formFileName);		  
		  request.setParameter("populatePolicy", policy.toString());		  
		  		  
		  ActionProxy proxy = getActionProxy("/SynchronizeOMCCatalogProcess");
	 
		  BeanCommunitySynchronizeOMCCatalog action = (BeanCommunitySynchronizeOMCCatalog) proxy.getAction();
		  action.setFormFile(myFile);
		  action.setPopulatePolicy(policy);
		  
		  action.setValidationActiveUser("testuser");
		  CommunitiesAccessor.reset();
		  ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		  action.setSelectedCommunity(new CommunityHandle(action.getLoggedUserProfile(),c));
		  action.getSelectedCommunity().updateFull();
		  
		  Integer expectedNbElsBefore = 4;
		  assertEquals("Catalog contents before synchronizing not as expected", expectedNbElsBefore,
				  (Integer)action.getSelectedCatalog().getElementsCount());
		  
		  String result = proxy.execute();	      
		  Integer expectedNbElsAfter = expectedNbElsBefore+1;
		  
		  assertEquals("Result returned form executing the action was not success but it should have been.", 
					BeanProcessResult.BeanProcess_SUCCESS.toString(), result);		  		  
		  
		  assertEquals("Catalog contents not as expected after synchronization", expectedNbElsAfter,
				  (Integer)action.getSelectedCatalog().getElementsCount());	 
		  
		  // Catalog import added new terms which shall be removed from the community so that
		  // it does not corrupt data for next tests
		  Iterator<ICommunityTerm> itTerms = c.getTerms().iterator(); 
		  List<ICommunityTerm> termsToDelete = new ArrayList<ICommunityTerm>();
		  List<IElement> elsToDelete = new ArrayList<IElement>();
		  while (itTerms.hasNext()) {
			  ICommunityTerm curTerm = itTerms.next();
			  if (curTerm.getTermId()>666) {
				  Iterator<Integer> itElid = c.getElementsIdsByTerm(curTerm.getTermId()).iterator();
				  while (itElid.hasNext()) { elsToDelete.add(c.getElement(itElid.next())); }
				  termsToDelete.add(curTerm);
			  }
		  }
		  Iterator<IElement> itEls = elsToDelete.iterator();
		  itTerms = termsToDelete.iterator();
		  while (itEls.hasNext()) {
			  IElement curEl = itEls.next();
			  curEl.updateFull(action.getLoggedUserProfile());
			  curEl.delete(action.getLoggedUserProfile());
		  }
		  while (itTerms.hasNext()) { itTerms.next().delete(action.getLoggedUserProfile()); }
		  c.updateFull(action.getLoggedUserProfile());
		  
	    }	
	  
	    
	  /**
	   * Synchronize catalog contents with Open Metaindex Catalog file (OMC) 
	   */
	  public void testUploadFromCSV() throws Exception {
		  
		  List<String> testCsvCatalogLines=new ArrayList<String>();
		  testCsvCatalogLines.add("# title;name;age;dog;graou;");
		  testCsvCatalogLines.add("ze Title; a name  ;  42;Waf;gnak;");
		  testCsvCatalogLines.add("Le titre;personne;;Jerry;;");
		  testCsvCatalogLines.add("# a comment hehe");
		  testCsvCatalogLines.add("LastBut;Not least;007;;Kurt Cobain;");
		  
		  String communityIdName="Test Community";
		  Integer formCatalogId=0;
		  String formFileContentType="text/csv";
		  String formFileName="testfilecatalog.csv";
		  String csvMappingStr="";
		  String baseElementId="1010";
		  csvMappingStr+=BeanCommunityUploadCSVCatalog.CSV_MAPPING_ID_ELEMENT_NAME+"="+"1;";
		  csvMappingStr+=BeanCommunityUploadCSVCatalog.CSV_MAPPING_ID_ELEMENT_COMMENT+"="+"2;";
		  // mapping between Template's metadata ids and CSV column index
		  csvMappingStr+="1010=2;1011=3;1012=4;";
		                                         	
		  File formFile = File.createTempFile(formFileName,"");
		  FileUtils.writeLines(formFile, "UTF-8", testCsvCatalogLines);
		  
		  POPULATE_POLICY policy = POPULATE_POLICY.ALWAYS_CREATE;
		  
		  request.setParameter("catalogId", formCatalogId.toString());
		  request.setParameter("formFileContentType", formFileContentType);		  
		  request.setParameter("formFileName", formFileName);
		  request.setParameter("csvMappingStr", csvMappingStr);		  
		  request.setParameter("baseElementId", baseElementId);
		  request.setParameter("populatePolicy", policy.toString());		  
		  		  
		  ActionProxy proxy = getActionProxy("/uploadCSVCatalogProcess");
	 		  
		  BeanCommunityUploadCSVCatalog action = (BeanCommunityUploadCSVCatalog) proxy.getAction();
		  action.setFormFile(formFile);
		  action.setPopulatePolicy(policy);
		  
		  action.setValidationActiveUser("testuser");
		  CommunitiesAccessor.reset();
		  ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		  action.setSelectedCommunity(new CommunityHandle(action.getLoggedUserProfile(),c));
		  action.getSelectedCommunity().updateFull();
		  action.getSelectedCommunity().getElement(new Integer(baseElementId)).updateFull();
		  Integer expectedNbElsBefore = 5;
		  assertEquals("Catalog contents before uploading not as expected", expectedNbElsBefore,
				  (Integer)action.getSelectedCatalog().getElementsCount());
		  
		  List<IElement> existingElementsBefore = new ArrayList<IElement>();
		  Iterator<IElement> it = c.getElements().iterator();
		  while (it.hasNext()) { existingElementsBefore.add(it.next()); }
		  
		  String result = proxy.execute();	      
		  Integer expectedNbElsAfter = expectedNbElsBefore+3;
		  		  
		  assertEquals("Result returned form executing the action was not success but it should have been.", 
					BeanProcessResult.BeanProcess_SUCCESS.toString(), result);		  		  
		  
		  assertEquals("Catalog contents not as expected after upload", expectedNbElsAfter,
				  (Integer)action.getSelectedCatalog().getElementsCount());	 
		  
		  // Catalog import added new elements which shall be removed from the community so that
		  // it does not corrupt data for next tests
		  List<IElement> elsToDelete = new ArrayList<IElement>();
		  Iterator<IElement> it2 = c.getElements().iterator();
		  while (it2.hasNext()) {
			  IElement el = it2.next();
			  if (!existingElementsBefore.contains(el)) { elsToDelete.add(el); }
		  }
		  Iterator<IElement> itEls = elsToDelete.iterator();
		  while (itEls.hasNext()) {
			  IElement curEl = itEls.next();
			  curEl.updateFull(action.getLoggedUserProfile());
			  curEl.delete(action.getLoggedUserProfile());
		  }
		  c.updateFull(action.getLoggedUserProfile());
	  	    
}
	  
}
