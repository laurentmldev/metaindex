package metaindex.data.catalog.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;

import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.catalog.AllElementsCatalog;
import metaindex.data.catalog.ICatalog;
import metaindex.data.community.CommunityTerm;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunityTerm;
import metaindex.data.community.ICommunityTermData;
import metaindex.data.community.TermVocabularySet;
import metaindex.data.community.beans.BeanCommunity;
import metaindex.data.dataset.Dataset;
import metaindex.data.dataset.IDataset;
import metaindex.data.element.Element;
import metaindex.data.element.IElement;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.Metadata;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;
import metaindex.dbaccess.IGenericEncodable.POPULATE_POLICY;
import metaindex.dbaccess.IGenericEncodable.UnableToPopulateException;

import java.io.File;
import org.apache.commons.io.FileUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths; 
 
public class BeanCommunitySynchronizeOMCCatalog extends BeanCommunityUploadCSVCatalog {
 
	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanCommunitySynchronizeOMCCatalog.class);
	
	   	
	@Override 
  	public String execute() throws Exception {
		
		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;
		
		try {
			List<String> lines= Files.readAllLines(Paths.get(getFormFile().getCanonicalPath()));
			
			String json = lines.get(0);			
			JSONObject jsonobject = new JSONObject(json);
			
			parseJsonCatalog(this.getLoggedUserProfile(),jsonobject,this.getPopulatePolicy());
			
		} catch (Exception e) {
			log.error("Unable to use uploaded file for populating catalog "+getCatalogId()+": "+e.getMessage());
			e.printStackTrace();
			status =  BeanProcessResult.BeanProcess_ERROR;
		}
		
		/* Exemple how to simply copy file in server local folder
		 destPath = "/tmp";

	      try{
	     	 //log.info("### Src File name: " + myFile);
	     	 //log.info("### Dst File name: " + myFileFileName);
	     	   	 
	     	 File destFile  = new File(destPath, myFileFileName);
	    	 FileUtils.copyFile(myFile, destFile);
	  
	    	 //log.info("### upload done!");
	      }catch(Exception e){
	         e.printStackTrace();
	         //status=BeanProcessResult.BeanProcess_ConstraintERROR;
	      }
		 */
			
			
		return status.toString();
	}
	
	private void parseJsonCatalog(IUserProfileData activeUser, JSONObject json, 
										 metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY policy) 
															throws UnableToPopulateException, DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException 
	{
		
		ICommunity community = CommunitiesAccessor.getCommunity(this.getSelectedCommunity().getCommunityId());
		ICatalog catalog = community.getCatalog(this.getSelectedCatalog().getCatalogId());
			
		List<IElement> updatedElements = new ArrayList<IElement>();
		
		//Map<Integer, IMetadata> curMetadatas =  catalog.getMetadatas();
		
		JSONObject jsonterms= (JSONObject) json.get("terms");
		JSONObject jsonelements = (JSONObject) json.get("elements");
		JSONObject jsondatasets = (JSONObject) json.get("datasets");
		JSONObject jsonmetadatas = (JSONObject) json.get("metadatas");
			
		// ID in Json data might change when uploaded into DB depending on the applied policy
		// We must keep track of this change so that for example a dataset pointing originally to
		// element id 1000 in json file will actually point to newly created element 1667 in DB
		Map<Integer,Integer> json2dbTermIdMap = new HashMap<Integer,Integer>();
		Map<Integer,Integer> json2dbElementIdMap = new HashMap<Integer,Integer>();
		Map<Integer,Integer> json2dbDatasetIdMap = new HashMap<Integer,Integer>();
		
		List<ICommunityTerm> newTerms = parseJsonTerms(activeUser,catalog,json2dbTermIdMap,jsonterms,policy);
		List<IElement> newElements = parseJsonElements(activeUser,catalog,updatedElements, json2dbElementIdMap,jsonelements,policy);
		parseJsonDatasets(activeUser,catalog,updatedElements, json2dbElementIdMap,json2dbDatasetIdMap, jsondatasets,policy);
		parseJsonMetadata(activeUser,catalog,updatedElements, json2dbElementIdMap,json2dbDatasetIdMap, json2dbTermIdMap,jsonmetadatas,policy);
		
		
		Iterator<ICommunityTerm> itTerms = newTerms.iterator();
		while (itTerms.hasNext()) { community.addTerm(itTerms.next()); }
		
		community.createFullAllElements(getLoggedUserProfile(), newElements);
		//community.commitFullAllElements(getLoggedUserProfile(), updatedElements);
				
	}


	private void parseJsonMetadata(IUserProfileData activeUser,
			ICatalog catalog,
			List<IElement> updatedElements, 
			Map<Integer,Integer> json2dbElementIdMap,
			Map<Integer,Integer> json2dbDatasetIdMap,
			Map<Integer,Integer> json2dbTermIdMap,
			JSONObject jsonmetadata, 
			 metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY policy) 
								throws UnableToPopulateException {
		Boolean alreadyDefinedInCatalog = false;
		
		// Importing datasets
		Iterator<String> it2 = jsonmetadata.keys();
		while (it2.hasNext()) {
			
			Integer jsonMetadataId = new Integer(it2.next());
			JSONObject curJsonMetadata = jsonmetadata.getJSONObject(jsonMetadataId.toString());
			Integer jsonDatasetId = curJsonMetadata.getInt("datasetId");
			IDataset parentDataset = catalog.getCommunityData().getDataset(json2dbDatasetIdMap.get(jsonDatasetId));
			if (parentDataset==null) { throw new UnableToPopulateException("Unable to get json dataset '"+jsonDatasetId
																+"' (local id="+json2dbDatasetIdMap.get(jsonDatasetId)+")"
																+" for adding json metadata '"+jsonMetadataId+"'");}
			//IElement parentElement = parentDataset.getParentElementData();
			IMetadata uploadedMetadata=null;
			try { uploadedMetadata = parentDataset.getMetadata(jsonMetadataId); }
			catch (DataAccessErrorException e) 
			{ 
				// nothing special, if not found the policy will decide what to do
			}
			alreadyDefinedInCatalog = uploadedMetadata!=null;
			// various behaviours depending on required populate policy
			// Replace existing only : ignore if missing
			if (!alreadyDefinedInCatalog && policy==metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY.REPLACE_EXISTING_ONLY) { continue; }
			
			// Create a new one
			else if (
						policy==metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY.ALWAYS_CREATE ||
						!alreadyDefinedInCatalog 
							&& policy==metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY.REPLACE_EXISTING_AND_CREATE_WHEN_NEW)
			{
				uploadedMetadata = new Metadata(catalog.getCommunityData());
				uploadedMetadata.setMetadataId(CommunitiesAccessor.getNewMetadataId());				
				
			}
			// Reuse existing one
			else if (alreadyDefinedInCatalog 
							&& (policy==metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY.REPLACE_EXISTING_AND_CREATE_WHEN_NEW
							||  policy==metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY.REPLACE_EXISTING_ONLY)) {
				// nothing special
																
			}
			// Otherwise ignore current Json entry
			else { uploadedMetadata=null; }
			
			// Finalize uploaded metadata contents
			if (uploadedMetadata!=null) {
				uploadedMetadata.setMetadataId(CommunitiesAccessor.getNewMetadataId());								
				uploadedMetadata.populateFromJson(activeUser, curJsonMetadata, policy);								
				uploadedMetadata.setParentDataset(parentDataset);		
				uploadedMetadata.setTermId(json2dbTermIdMap.get(uploadedMetadata.getTermId()));
				if (!updatedElements.contains(parentDataset.getParentElement())) {
					updatedElements.add(parentDataset.getParentElement());									
				}
			}
		}
		
	}
	

	private void parseJsonDatasets(IUserProfileData activeUser,
			ICatalog catalog,
			List<IElement> updatedElements, 
			Map<Integer,Integer> json2dbElementIdMap,
			Map<Integer,Integer> json2dbDatasetIdMap,
			JSONObject jsondatasets, 
			 metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY policy) 
								throws UnableToPopulateException {
		Boolean alreadyDefinedInCatalog = false;
		
		// Importing datasets
		Iterator<String> it2 = jsondatasets.keys();
		while (it2.hasNext()) {
			
			Integer jsonDatasetId = new Integer(it2.next());
			JSONObject curJsonDataset = jsondatasets.getJSONObject(jsonDatasetId.toString());
			Integer jsonElementId = curJsonDataset.getInt("elementId");
			IDataset uploadedDataset=null;
			IElement parentElement = catalog.getCommunityData().getElement(json2dbElementIdMap.get(jsonElementId));
			
			try { uploadedDataset = parentElement.getDataset(jsonDatasetId); }
			catch (DataAccessErrorException e) 
			{ 
				// nothing special, if not found the policy will decide what to do
			}
			alreadyDefinedInCatalog = uploadedDataset!=null;
			// various behaviours depending on required populate policy
			// Replace existing only : ignore if missing
			if (!alreadyDefinedInCatalog && policy==metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY.REPLACE_EXISTING_ONLY) { continue; }
			
			// Replace and Create : if missing we create it
			else if (
						policy==metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY.ALWAYS_CREATE ||
						!alreadyDefinedInCatalog 
							&& policy==metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY.REPLACE_EXISTING_AND_CREATE_WHEN_NEW)
			{
				
				// create new dataset
				uploadedDataset = new Dataset(catalog.getCommunityData());
				uploadedDataset.populateFromJson(activeUser, curJsonDataset, 
													metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY.ALWAYS_CREATE);
				uploadedDataset.setDatasetId(CommunitiesAccessor.getNewDatasetId());				
				uploadedDataset.setParentElement(parentElement);				
				json2dbDatasetIdMap.put(jsonDatasetId, uploadedDataset.getDatasetId());
				parentElement.addDataset(uploadedDataset);
				if (!updatedElements.contains(parentElement)) { updatedElements.add(parentElement); }
			} else { 
				json2dbDatasetIdMap.put(jsonDatasetId, jsonDatasetId);
				uploadedDataset.populateFromJson(activeUser, curJsonDataset, metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY.ALWAYS_CREATE);				
				uploadedDataset.setDatasetId(json2dbDatasetIdMap.get(jsonDatasetId));	
				uploadedDataset.setParentElement(parentElement);				
				if (!updatedElements.contains(parentElement)) { updatedElements.add(parentElement); }
			}									
		}
		
	}
	
	private List<IElement> parseJsonElements(IUserProfileData activeUser,
				ICatalog catalog,
				List<IElement> updatedElements,
				Map<Integer,Integer> json2dbElementIdMap,
				JSONObject jsonelements, 
				 metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY policy) 
									throws UnableToPopulateException {
		List<IElement> result = new ArrayList<IElement>();
		Map<Integer,IElement> curElements = catalog.getElementsMap();
		
		// at the time of parsing element, the referenced base might not have been
		// parsed yet, if so we remember them so that we can set it afterwards				
		List<IElement> orphanTemplatedElements = new ArrayList<IElement>();
		
		Boolean alreadyDefinedInCatalog = false;

		// Importing elements
		Iterator<String> it1 = jsonelements.keys();
		while (it1.hasNext()) {
			
			Integer jsonElementId = new Integer(it1.next());
			json2dbElementIdMap.put(jsonElementId, jsonElementId);
			
			JSONObject curJsonElement = jsonelements.getJSONObject(jsonElementId.toString());
			IElement uploadedElement=null;
			try { uploadedElement = curElements.get(jsonElementId); }
			catch (DataAccessErrorException e) { /* nothing special, if not found the policy will decide what to do*/ }
			alreadyDefinedInCatalog = uploadedElement!=null;
			
			// various behaviours depending on required populate policy
			// Replace existing only : ignore if missing
			if (!alreadyDefinedInCatalog && policy==metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY.REPLACE_EXISTING_ONLY) { continue; }
			
			// Replace and Create : if missing we create it
			else if ((!alreadyDefinedInCatalog 
							&& policy==metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY.REPLACE_EXISTING_AND_CREATE_WHEN_NEW)
							|| policy==metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY.ALWAYS_CREATE) {
				
				uploadedElement = new Element(catalog.getCommunityData());
				uploadedElement.setElementId(CommunitiesAccessor.getNewElementId());
				uploadedElement.setName("__tmp__populating__catalog__");				
				json2dbElementIdMap.put(jsonElementId, uploadedElement.getElementId());
				result.add(uploadedElement);
				catalog.getCommunityData().addElement(activeUser, uploadedElement);
				// add new element as a static one only if catalog is not a pure virtual one 				
				if (!catalog.isVirtual()) { 
					catalog.addStaticElement(activeUser,uploadedElement.getElementId());
				}
			}
			// simple update of an existing element
			else { updatedElements.add(uploadedElement); }
			
			uploadedElement.populateFromJson(activeUser, curJsonElement, metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY.ALWAYS_CREATE);
			uploadedElement.setElementId(json2dbElementIdMap.get(jsonElementId));
			if (uploadedElement.isTemplated()) {
				Integer oldBaseElementId = uploadedElement.getTemplateRefElementId();
				Integer newBaseElementId = json2dbElementIdMap.get(oldBaseElementId);
				if (newBaseElementId==null) { orphanTemplatedElements.add(uploadedElement); }
				else { uploadedElement.setTemplateRefElementId(newBaseElementId); }
				 
			} 
						
			
			log.debug("parsed element "+uploadedElement.getElementId()+".");			
		}
		
		// now that we have parsed all elements 
		// we handle remaining orphan templated elements
		Iterator<IElement> it = orphanTemplatedElements.iterator();
		while (it.hasNext()) { 
			IElement curE = it.next();
			Integer oldBaseElementId = curE.getTemplateRefElementId();
			Integer newBaseElementId = json2dbElementIdMap.get(oldBaseElementId);
			if (newBaseElementId==null) { 
				log.error("Template element '"+oldBaseElementId+"' is missing in the uploaded data."); 
			}
			else {
				curE.setTemplateRefElementId(newBaseElementId);
				result.add(curE);
			}
		}
			
		return result;
	}
		
	private List<ICommunityTerm> parseJsonTerms(IUserProfileData activeUser,
			ICatalog catalog,
			Map<Integer,Integer> json2dbTermIdMap,
			JSONObject jsonterms, 
			 metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY policy) 
								throws UnableToPopulateException {
		
		List<ICommunityTerm> result = new ArrayList<ICommunityTerm>(); 
		
		ICommunity community = catalog.getCommunityData();
		
		Iterator<String> itTerms = jsonterms.keys();
		while (itTerms.hasNext()) {
			Integer jsonTermId = new Integer(itTerms.next());
			json2dbTermIdMap.put(jsonTermId, jsonTermId);
			JSONObject curJsonTerm = jsonterms.getJSONObject(jsonTermId.toString());
			String curJsonTermName = curJsonTerm.getString("idName");
			Integer curJsonTermDatatypeId = curJsonTerm.getInt("datatypeId");
			ICommunityTerm localTerm=null;
		
			// check if datatype matches, if not, that means that the imported json term has an homonym in the
			// current community but with a different datatype. If so we throw an error.
			localTerm = community.getTermData(curJsonTermName);
			if (localTerm==null) {
				// Term not found -> we have to create it
				List<TermVocabularySet> emptyVocabulary = new ArrayList<TermVocabularySet>();
				localTerm = new CommunityTerm(community);
				localTerm.setTermId(CommunitiesAccessor.getNewTermId());
				localTerm.setIdName(curJsonTermName);
				localTerm.setDatatypeId(curJsonTermDatatypeId);
				localTerm.setVocabularySets(emptyVocabulary);
				json2dbTermIdMap.put(jsonTermId,  localTerm.getTermId());
				result.add(localTerm);
			}
			Integer localTermDataTypeId = localTerm.getDatatypeId();
			// Datatypes mismatch although theu have the same name
			// so we throw an error
			if (localTermDataTypeId!=curJsonTermDatatypeId) {
				throw new UnableToPopulateException("Trying to import term '"+curJsonTermName+"' of datatype "
															+community.getDatatypeName(curJsonTermDatatypeId)
															+" while community already have this term but with datatype "
															+community.getDatatypeName(localTermDataTypeId));
			}	
			json2dbTermIdMap.put(jsonTermId, localTerm.getTermId());
			
		}
		return result;
	}
	
	
	/*
	
	
	// Importing metadata
	Iterator<String> it3 = jsonmetadatas.keys();
	while (it3.hasNext()) {
		
		
		Integer jsonMetadataId = new Integer(it3.next());
		Integer jsonDatasetId = null;
		Integer jsonTermId = null;
		
		JSONObject curJsonMetadata = jsonmetadatas.getJSONObject(jsonMetadataId.toString());
		IMetadata uploadedMetadata=null;
		try { uploadedMetadata = curMetadatas.get(jsonMetadataId); }
		catch (DataAccessErrorException e) 
		{  
			nothing special, if not found the policy will decide what to do 
		}
		alreadyDefinedInCatalog = uploadedMetadata!=null;
		// various behaviours depending on required populate policy
		// Replace existing only : ignore if missing
		if (!alreadyDefinedInCatalog && policy==metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY.REPLACE_EXISTING_ONLY) { continue; }
		
		// Replace and Create : if missing we create it
		else if (
					policy==metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY.ALWAYS_CREATE ||
					!alreadyDefinedInCatalog 
						&& policy==metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY.REPLACE_EXISTING_AND_CREATE_WHEN_NEW)
		{
			
			uploadedMetadata = new Metadata(this.getCommunityData());
			uploadedMetadata.populateFromJson(activeUser, curJsonMetadata, 
												metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY.ALWAYS_CREATE);
			jsonDatasetId=uploadedMetadata.getDatasetId();
			uploadedMetadata.setMetadataId(json2dbDatasetIdMap.get(jsonDatasetId));
			jsonTermId=uploadedMetadata.getTermId();
			Integer matchingTermId=json2dbTermIdMap.get(jsonTermId);
			if (matchingTermId==null) {
				throw new UnableToPopulateException("While uploading dataset, metadata '"+jsonMetadataId+"' references to term id '"+jsonTermId+"'"
																										+" which is not defined in the imported data.");
			}
			uploadedMetadata.setTermId(json2dbTermIdMap.get(jsonTermId));
			uploadedMetadata.create(activeUser);
			json2dbMetadataIdMap.put(jsonMetadataId, uploadedMetadata.getMetadataId());
			
		} else { json2dbMetadataIdMap.put(jsonMetadataId, jsonMetadataId); }
		
		uploadedMetadata.populateFromJson(activeUser, curJsonMetadata, metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY.ALWAYS_CREATE);
		jsonDatasetId=uploadedMetadata.getDatasetId();
		uploadedMetadata.setMetadataId(json2dbMetadataIdMap.get(jsonMetadataId));	
		jsonTermId=uploadedMetadata.getTermId();
		uploadedMetadata.setTermId(json2dbTermIdMap.get(jsonTermId));
		uploadedMetadata.setDatasetId(json2dbDatasetIdMap.get(jsonDatasetId));
		if (json2dbTermIdMap.get(uploadedMetadata.getTermId()) != null) {
			uploadedMetadata.setTermId(json2dbTermIdMap.get(uploadedMetadata.getTermId()));
		}
		uploadedMetadata.commitFull(activeUser);
	}
*/
}
