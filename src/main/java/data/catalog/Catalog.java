package metaindex.data.catalog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.IObserver;
import metaindex.data.community.ACommunitySubdata;
import metaindex.data.community.CommunityTerm;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunityTerm;
import metaindex.data.community.TermVocabularySet;
import metaindex.data.dataset.Dataset;
import metaindex.data.dataset.DatasetHandle;
import metaindex.data.dataset.IDataset;
import metaindex.data.dataset.IDatasetHandle;
import metaindex.data.element.Element;
import metaindex.data.element.ElementHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.management.CatalogsAccessor;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.metadata.Metadata;
import metaindex.data.metadata.MetadataHandle;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IGenericEncodable.UnableToPopulateException;
import metaindex.dbaccess.accessors.ACatalogAccessor;
import metaindex.dbaccess.accessors.ADatasetAccessor;
import metaindex.dbaccess.accessors.AElementAccessor;
import metaindex.dbaccess.accessors.AGuiLanguagesAccessor;
import metaindex.dbaccess.accessors.AGuiThemesAccessor;
import metaindex.dbaccess.accessors.AMetadataAccessor;
import metaindex.dbaccess.accessors.AMetaindexAccessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Bean storing community data
 * @author laurent
 *
 */
public class Catalog extends ACommunitySubdata<ICatalogContents> implements ICatalog {

	private Log log = LogFactory.getLog(Catalog.class);

		
		
	private boolean isSynchronized=false;
	private int catalogId=0;
	private String name="";	
	private String comment="";	
	private String searchQuery="";
	
	// flag used to recognize real catalogs from virtual dynamic ones (like 'All' catalog)
	private boolean isVirtual=false;
	private Map<Integer,IElement>  staticElementsMap = new HashMap<Integer,IElement>();
	private Map<Integer,IElement>  dynamicElementsMap = new HashMap<Integer,IElement>();
	

	private List<IElement> elements = new ArrayList<IElement>();
	private Map<Integer,IElement> elementsMap = new HashMap<Integer,IElement>();


	public Catalog(ICommunity myCommunity) {
		super(myCommunity,CommunitiesAccessor.getDataAccessors());			
	}
	
	public void dump(String depthStr) {
		log.error(depthStr+"Catalog "+this.getCatalogId()+" : "+this.getName());
		Iterator<IElement> it = this.getElements().iterator();		
		while (it.hasNext()) {
			IElement e = it.next();
			e.dump(depthStr+"  ");
		}
	}
	

	@Override
	public void checkDataDBCompliance() throws DataAccessConstraintException {
		checkCompliantWithDBSmallString("field 'name' of catalog '"+this.getName()+"'",this.getName());
		checkCompliantWithDBSmallString("field 'comment' of catalog '"+this.getName()+"'",this.getComment());
	}
	
  	@Override
  	public boolean isIdentified() {
  		return this.getCatalogId()!=0;
  	}
  	
  	@Override
	public void delete(IUserProfileData activeUser)
			throws DataAccessErrorException,DataAccessConstraintException {
  		
  		this.getCatalogDBAccessor().deleteFromDB(activeUser, this);		
  	}
	
	@Override
	public void update(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException {

		this.getCatalogDBAccessor().refreshFromDB(activeUser, this);	
		isSynchronized=true;
	}

	/**
	 * Ensure that given Element (id) exists in current catalog.
	 * If not, return the id of the first element of the catalog.
	 * @param elementId
	 * @return
	 */
	public Integer confirmSelectedElement(Integer elementId) {

		// if currently selected element has been deleted or was null, we switch selection to the first element of the catalog
		if (this.getElements().size()==0) { return 0; }
		else if (elementId==0 || this.getElement(elementId)==null) { return this.getElements().get(0).getElementId(); }
		else { return elementId; }
	}
	
	@Override
	public void updateFull(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException, DataReferenceErrorException {
		this.update(activeUser);
				
		// (re)load all metadata, datasets, elements of this catalog
		loadElementsRefsFromDB(activeUser);		
	}
		
	@Override
	public List<Integer> getStaticElementsList(IUserProfileData activeUser) {
		return this.getCatalogDBAccessor().getCatalogStaticElementsIds(activeUser, this);
	}
	@Override
	public List<Integer> getDynamicElementsList(IUserProfileData activeUser) {
		return this.getCatalogDBAccessor().getCatalogDynamicElementsIds(activeUser, this);
	}
	
	
	/**
	 * Will retrieve the list of all elements belonging to this catalog
	 * (both dynamic, coming from an SQL call applying the searchQuery,
	 * statics, explicitly listed in static catalog table.
	 * 
	 * When calling this method, the community should have loaded all elements in memory.
	 * The catalog will just reference objects stored by the community object and matching its
	 * list of query.
	 * @param username the active user name
	 * @throws DataAccessErrorException if some matching element was not loaded in the community
	 * @throws DataAccessConstraintException
	 * @throws DataReferenceErrorException 
	 */
	public void loadElementsRefsFromDB(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException, DataReferenceErrorException {
				
		String missing="";
		int nbMissing=0;
		
		elements.clear();
		elementsMap.clear();
		staticElementsMap.clear();
		dynamicElementsMap.clear();
		
		// retrieve  elements lists		
		List<Integer> staticElementsIds= getStaticElementsList(activeUser);
		List<Integer> dynamicElementsIds= getDynamicElementsList(activeUser);
		
		// we just retrieved the IDs,
		// we will now get elements objects themselves, which are (should be) already loaded in community
				
		// Retrieving Static elements
		Iterator<Integer> it = staticElementsIds.iterator();
		while (it.hasNext()) {
			Integer curId = it.next();
			IElement el = getCommunityData().getElement(curId);

			// if not found we store it for a consistent error message
			if (el==null) { nbMissing++; missing+=curId+" "; } 
			else {
				// first adding it to the map, thus we remove duplicates automatically
				elementsMap.put(el.getElementId(),el);
				staticElementsMap.put(el.getElementId(),el);
			}
		}
	
		// Retrieving Dynamic elements
		// Don't add them if they are already in catalog as static
		it = dynamicElementsIds.iterator();
		while (it.hasNext()) {
			Integer curId = it.next();
			IElement el = null;
			try {
				el = getCommunityData().getElement(curId);
				// only add if not already added as a static element 
				if (staticElementsMap.get(el.getElementId())==null) {
						// if not found we store it for a consistent error message				
						elementsMap.put(el.getElementId(),el);
						dynamicElementsMap.put(el.getElementId(),el);
				}
			} catch(DataAccessErrorException e) {
				// element does not exsit
				nbMissing++; missing+=curId+" "; 
			}
			
		}
		// if some could not be found, that means our DB is probably not consistent! (big troubles)
		if (nbMissing>0) { throw new DataAccessErrorException(nbMissing+" element(s) matched catalog '"+this.getName()
										+"'but not found in community '"+getCommunityData().getIdName()+"' : "+missing); }
		
		// build the elements array and sort it alphabetically 
		elements=new ArrayList<IElement>();
		Iterator<Integer> itKeys = elementsMap.keySet().iterator();
		while (itKeys.hasNext()) {
			IElement cur = elementsMap.get(itKeys.next());
			elements.add(cur);
		}
		
		elements.sort(new Element.NameComparator());
		
		this.notifyObservers();
	}
	
	@Override
	public void commit(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException {
		this.getCatalogDBAccessor().storeIntoDB(activeUser, this);			
	}
	@Override
	public void commitFull(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException {
		this.commit(activeUser);
		Iterator<IElement> it = this.getElements().iterator();
		while (it.hasNext()) { it.next().commitFull(activeUser); }
		
	}
	
	@Override
	public void create(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		this.getCatalogDBAccessor().createIntoDB(activeUser, this);
	}

	/**
	 * Compute list of elements
	 * @return
	 */
	@Override
	public List<IElement> getElements() {
		return elements;
	}
	
	@Override
	public Map<Integer, IElement> getElementsMap() {
		return elementsMap;
	}
	
	@Override
	public List<IElementHandle> getUserElements(IUserProfileData user) {
		
		// TODO maybe optimize that?!	
		List<IElementHandle> result = new ArrayList<IElementHandle>();
		Iterator<IElement> it = elements.iterator();
		while(it.hasNext()) {
			IElement el = it.next();
			result.add(new ElementHandle(user,el));
		}
		
		return result;
	}
	
	
	@Override
	public int getElementsCount() {			
		return elements.size(); 
	}

	@Override
	public void addStaticElement (IUserProfileData activeUser, int elementToAddId) throws DataAccessErrorException, DataAccessConstraintException 
	{
		IElement elementToAdd = this.getCommunityData().getElement(elementToAddId);
		
		// if element does not belong to this community, throw an error
		if (elementToAdd==null) { throw new DataAccessConstraintException("Element '"+elementToAddId
								+"' not found in community '"+this.getCommunityData().getIdName()+"'"); }
			
		try {		
			// if given element is already a static element of this catalog, then we just silently do nothing
			if (isStaticElement(elementToAddId)) { return; }
		} catch (DataAccessErrorException e) { /* nothing to do here, just testing if element is already referenced */}
		
		elementToAdd.addAssociation(activeUser, this); 	
		
		try { this.loadElementsRefsFromDB(activeUser); } catch (DataReferenceErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DataAccessErrorException(e);
		}
		
		
	}
	@Override
	public void removeStaticElements (IUserProfileData activeUser, List<IElement> elementsToRemove) 
			throws DataAccessErrorException, DataAccessConstraintException 
	{
			this.getElementDBAccessor().removeAssociation(activeUser, elementsToRemove, this);		 		
	}
	
	@Override
	public void removeStaticElement (IUserProfileData activeUser, int elementToAddId) throws DataAccessErrorException, DataAccessConstraintException 
	{
		try {			
			IElement elementToRemove = this.getElement(elementToAddId);		
			// if element does not belong to this community, throw an error
			if (elementToRemove==null || !this.isStaticElement(elementToAddId)) { return; }
			
			elementToRemove.removeAssociation(activeUser, this);
			
		} catch (DataAccessErrorException e) { /* nothing to do here, just testing if element is already referenced */}
		
		try { this.loadElementsRefsFromDB(activeUser); } catch (DataReferenceErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DataAccessErrorException(e);
		}			 
		 		
	}
	
	public boolean isVirtual() {
		return isVirtual;
	}

	public void setVirtual(boolean isVirtual) {
		this.isVirtual = isVirtual;
	}


	/**
	 * @return the catalogId
	 */
	@Override
	public int getCatalogId() {
		return catalogId;
	}

	/**
	 * @param catalogId the catalogId to set
	 */
	@Override
	public void setCatalogId(int catalogId) {
		this.catalogId = catalogId;
	}


	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the comment
	 */
	@Override
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the searchQuery as defined by the user
	 */
	@Override
	public String getSearchQuery() {
		return searchQuery;
	}

	/**
	 * @param searchQuery the searchQuery to set
	 */
	@Override
	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}

	@Override
	public boolean isDynamic() {
		return this.getSearchQuery().length()>0;
	}


	/**
	 * Retrieve the required element from the elements list of this catalog
	 * @param elementId the id (in DB) of the required element
	 * @throws DataAccessErrorException if element does not belong to the catalog
	 */
	public IElement getElement(int elementId) throws DataAccessErrorException {
		IElement result = elementsMap.get(elementId);
		if (result==null) { 
			throw new DataAccessErrorException("Element ID '"+elementId+"' not found in catalog '"+this.getName()+"' ('"+this.getCatalogId()+"')"); 
		}
		return result;
	}

	/**
	 * Say wether the given element belongs to the 'statically loaded' elements
	 * of this catalog.
	 * @param elementId
	 * @return
	 */
	public boolean isStaticElement(int elementId) {
		return (staticElementsMap.get(elementId)!=null);
	}
	/**
	 * Retrieve the amount of dynamic elements of this catalog
	 * @return
	 */
	public int getNbDynamicElements() {
		return dynamicElementsMap.size();
	}
	/**
	 * Retrieve the amount of static elements of this catalog
	 * @return
	 */	public int getNbStaticElements() {
		return staticElementsMap.size();
	}

	
	@Override
	public void invalidate() {
		isSynchronized=false;
		
	}

	@Override
	public boolean isSynchronized() {
		return isSynchronized;
	}

	@Override
	public ICatalog clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Clone not supported for Catalogs."); 
	}
	
	@Override
	public void populateFromJson(IUserProfileData activeUser, JSONObject json, 
					metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY policy) throws UnableToPopulateException {
		
		Map<Integer, IDataset> curDatasets = getDatasets();
		Map<Integer, IMetadata> curMetadatas =  getMetadatas();
		Map<Integer, IElement> curElements = this.elementsMap;
		
		JSONObject jsonterms= (JSONObject) json.get("terms");
		JSONObject jsonmetadatas = (JSONObject) json.get("metadatas");
		JSONObject jsondatasets = (JSONObject) json.get("datasets");
		JSONObject jsonelements = (JSONObject) json.get("elements");
		
		Boolean alreadyDefinedInCatalog = false;
		
		// Remember elements newly created so that we can add them as static elements
		// of current catalog once community has been updated
		List<IElement> elementsToAddToCatalog= new ArrayList<IElement>();
		
		// ID in Json data might change when uploaded into DB depending on the applied policy
		// We must keep track of this change so that for example a dataset pointing originally to
		// element id 1000 in json file will actually point to newly created element 1667 in DB
		Map<Integer,Integer> json2dbTermIdMap = new HashMap<Integer,Integer>();
		Map<Integer,Integer> json2dbElementIdMap = new HashMap<Integer,Integer>();
		Map<Integer,Integer> json2dbDatasetIdMap = new HashMap<Integer,Integer>();
		Map<Integer,Integer> json2dbMetadataIdMap = new HashMap<Integer,Integer>();
		// at the time of parsing element, the referenced base might not have been
		// parsed yet, if so we remember them so that we can set it afterwards
		List<IElement> orphanTemplatedElements = new ArrayList<IElement>();
		
		// Importing Terms
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
			localTerm = this.getCommunityData().getTermData(curJsonTermName);
			if (localTerm==null) {
				// Term not found -> we have to create it
				List<TermVocabularySet> emptyVocabulary = new ArrayList<TermVocabularySet>();
				ICommunityTerm newTerm = new CommunityTerm(this.getCommunityData());
				
				newTerm.setIdName(curJsonTermName);
				newTerm.setDatatypeId(curJsonTermDatatypeId);
				newTerm.setVocabularySets(emptyVocabulary);
				newTerm.create(activeUser);
				this.getCommunityData().addTerm(newTerm);
				try { this.getCommunityData().update(activeUser); } 
				catch (DataAccessErrorException | DataAccessConstraintException | DataReferenceErrorException e1) {
					throw new UnableToPopulateException("While adding new term '"+curJsonTermName+"' from imported data : "+e1.getMessage());
				}
				localTerm = this.getCommunityData().getTermData(curJsonTermName);
				json2dbTermIdMap.put(jsonTermId, localTerm.getTermId());
			}
			Integer localTermDataTypeId = localTerm.getDatatypeId();
			// Datatypes mismatch although theu have the same name
			// so we throw an error
			if (localTermDataTypeId!=curJsonTermDatatypeId) {
				throw new UnableToPopulateException("Trying to import term '"+curJsonTermName+"' of datatype "
															+this.getCommunityData().getDatatypeName(curJsonTermDatatypeId)
															+" while community already have this term but with datatype "
															+this.getCommunityData().getDatatypeName(localTermDataTypeId));
			}	
			json2dbTermIdMap.put(jsonTermId, localTerm.getTermId());
				
	
		}
		
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
				
				uploadedElement = new Element(this.getCommunityData());
				uploadedElement.setName("__tmp__populating__catalog__");
				uploadedElement.create(activeUser);
				
				json2dbElementIdMap.put(jsonElementId, uploadedElement.getElementId());
				
				// add new element as a static one only if catalog is not the "all elements" catalog 				
				if (this.getCatalogId()!=AllElementsCatalog.ALL_ELEMENTS_CATALOG_ID) { elementsToAddToCatalog.add(uploadedElement); }
			}
			
			uploadedElement.populateFromJson(activeUser, curJsonElement, metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY.ALWAYS_CREATE);
			uploadedElement.setElementId(json2dbElementIdMap.get(jsonElementId));
			if (uploadedElement.isTemplated()) {
				Integer oldBaseElementId = uploadedElement.getTemplateRefElementId();
				Integer newBaseElementId = json2dbElementIdMap.get(oldBaseElementId);
				if (newBaseElementId==null) { orphanTemplatedElements.add(uploadedElement); }
				else { uploadedElement.setTemplateRefElementId(newBaseElementId); }
				 
			}
			uploadedElement.commitFull(activeUser);
						
			log.debug("imported element "+uploadedElement.getElementId()+".");
			
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
			else { curE.setTemplateRefElementId(newBaseElementId); }
		}
		
		// refresh community data contents
		try {
			this.getCommunityData().updateFull(activeUser);
			Iterator<IElement> it2 = elementsToAddToCatalog.iterator(); 
			while (it2.hasNext()) { this.addStaticElement(activeUser, it2.next().getElementId()); }
		} catch (DataAccessErrorException | DataAccessConstraintException | DataReferenceErrorException e) {
			throw new metaindex.dbaccess.IGenericEncodable.UnableToPopulateException(e.getMessage());
		}
		
		// Importing datasets
		Iterator<String> it2 = jsondatasets.keys();
		while (it2.hasNext()) {
			
			
			Integer jsonDatasetId = new Integer(it2.next());
			Integer jsonElementId = null;
			
			JSONObject curJsonDataset = jsondatasets.getJSONObject(jsonDatasetId.toString());
			IDataset uploadedDataset=null;
			try { uploadedDataset = curDatasets.get(jsonDatasetId); }
			catch (DataAccessErrorException e) { /* nothing special, if not found the policy will decide what to do*/ }
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
				uploadedDataset = new Dataset(this.getCommunityData());
				uploadedDataset.populateFromJson(activeUser, curJsonDataset, 
													metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY.ALWAYS_CREATE);
				jsonElementId=uploadedDataset.getElementId();
				uploadedDataset.setElementId(json2dbElementIdMap.get(jsonElementId));
				uploadedDataset.create(activeUser);
				json2dbDatasetIdMap.put(jsonDatasetId, uploadedDataset.getDatasetId());
				
			} else { json2dbDatasetIdMap.put(jsonDatasetId, jsonDatasetId); }
			
			uploadedDataset.populateFromJson(activeUser, curJsonDataset, metaindex.dbaccess.IJsonEncodable.POPULATE_POLICY.ALWAYS_CREATE);
			jsonElementId=uploadedDataset.getElementId();
			uploadedDataset.setDatasetId(json2dbDatasetIdMap.get(jsonDatasetId));	
			uploadedDataset.setElementId(json2dbElementIdMap.get(jsonElementId));
			
			uploadedDataset.commitFull(activeUser);
		}
		
		// Importing metadata
		Iterator<String> it3 = jsonmetadatas.keys();
		while (it3.hasNext()) {
			
			
			Integer jsonMetadataId = new Integer(it3.next());
			Integer jsonDatasetId = null;
			Integer jsonTermId = null;
			
			JSONObject curJsonMetadata = jsonmetadatas.getJSONObject(jsonMetadataId.toString());
			IMetadata uploadedMetadata=null;
			try { uploadedMetadata = curMetadatas.get(jsonMetadataId); }
			catch (DataAccessErrorException e) { /* nothing special, if not found the policy will decide what to do*/ }
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

	}
	
	
	@Override
	public JSONObject encode() {
		
		JSONObject jsonterms = new JSONObject();
		JSONObject jsonmetadatas = new JSONObject();
		JSONObject jsondatasets = new JSONObject();
		JSONObject jsonelements = new JSONObject();
		
		List<ICommunityTerm> terms = this.getCommunityData().getTerms();
		List<IElement> els = this.getElements();
		Collection<IDataset> datasets = getDatasets().values();
		Collection<IMetadata> metadatas = getMetadatas().values();
		
		// Terms
		Iterator<ICommunityTerm> itTerm = terms.iterator();
		while (itTerm.hasNext()) {
			ICommunityTerm cur = itTerm.next(); 	
			jsonterms.put(new Integer(cur.getTermId()).toString(), cur.encode());			
		}
		// Metadatas
		Iterator<IMetadata> itMetadata = metadatas.iterator();
		while (itMetadata.hasNext()) {
			IMetadata cur = itMetadata.next(); 	
			jsonmetadatas.put(new Integer(cur.getMetadataId()).toString(), cur.encode());			
		}
		
		
		// Datasets
		Iterator<IDataset> itDataset = datasets.iterator();
		while (itDataset.hasNext()) {
			IDataset cur = itDataset.next(); 			
			jsondatasets.put(new Integer(cur.getDatasetId()).toString(), cur.encode());		
		}
		
		
		// Elements
		Iterator<IElement> itEl = els.iterator();
		while (itEl.hasNext()) {
			IElement cur = itEl.next(); 			
			jsonelements.put(new Integer(cur.getElementId()).toString(), cur.encode());
		}				
		
		JSONObject jsonglobal = new JSONObject();
		jsonglobal.put("terms", jsonterms);
		jsonglobal.put("metadatas", jsonmetadatas);
		jsonglobal.put("datasets", jsondatasets);
		jsonglobal.put("elements", jsonelements);
		
		return jsonglobal;
	}

	@Override
	public Map<Integer, IMetadata> getMetadatas() {
		Map<Integer, IMetadata> result = new HashMap<Integer,IMetadata>();
		Iterator<IDataset> it = getDatasets().values().iterator();
		while (it.hasNext()) {
			IDataset cur = it.next();
			Iterator<IMetadata> it2 = cur.getMetadata().iterator();
			while (it2.hasNext()) {
				IMetadata metadata = it2.next();
				result.put(metadata.getMetadataId(), metadata);
			}
		}
		return result;
	}

	@Override
	public Map<Integer, IDataset> getDatasets() {
		Map<Integer, IDataset> result = new HashMap<Integer,IDataset>();
		Iterator<IElement> it = this.getElements().iterator();
		while (it.hasNext()) {
			IElement cur = it.next();
			Iterator<IDataset> it2 = cur.getDatasets().iterator();
			while (it2.hasNext()) {
				IDataset dataset = it2.next();
				result.put(dataset.getDatasetId(), dataset);
			}
		}
		return result;
	}


	@Override
	public Map<Integer, IMetadataHandle> getUserMetadatas(IUserProfileData activeUser) {
		Map<Integer, IMetadataHandle> result = new HashMap<Integer,IMetadataHandle>();
		Iterator<IDataset> it = getDatasets().values().iterator();
		while (it.hasNext()) {
			IDataset cur = it.next();
			Iterator<IMetadata> it2 = cur.getMetadata().iterator();
			while (it2.hasNext()) {
				IMetadata metadata = it2.next();
				result.put(metadata.getMetadataId(), new MetadataHandle(activeUser,metadata));
			}
		}
		return result;
	}

	@Override
	public Map<Integer, IDatasetHandle> getUserDatasets(IUserProfileData activeUser) {
		Map<Integer, IDatasetHandle> result = new HashMap<Integer,IDatasetHandle>();
		Iterator<IElement> it = this.getElements().iterator();
		while (it.hasNext()) {
			IElement cur = it.next();
			Iterator<IDataset> it2 = cur.getDatasets().iterator();
			while (it2.hasNext()) {
				IDataset dataset = it2.next();
				result.put(dataset.getDatasetId(), new DatasetHandle(activeUser, dataset));
			}
		}
		return result;
	}

	
	@Override
	public void notifyObservers() {
		List<ICatalogHandle> myObservers = CatalogsAccessor.getCatalogHandles(this.getCommunityId(), this.getCatalogId());
		if (myObservers==null) { return; }
		Iterator<ICatalogHandle> it = myObservers.iterator();
		while (it.hasNext()) { it.next().notifyChange(this); }
		
	}

	@Override
	public void addObserver(ICatalogHandle newObserver) {		 
			CatalogsAccessor.addCatalogHandle(newObserver); 				
	}

	@Override
	public void removeObserver(ICatalogHandle oldObserver) {
		CatalogsAccessor.clear(this);		
	}

	@Override
	public ICommunity getCommunityData() {	
		try {
			return CommunitiesAccessor.getCommunity(this.getCommunityId());
		} catch (DataAccessErrorException | DataAccessConstraintException | DataReferenceErrorException e) {
			e.printStackTrace();
			return null;
		}
	}

}
