package metaindex.data.community;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.opensymphony.xwork2.ActionSupport;

import metaindex.data.AMultiLanguageMetaindexData;
import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.catalog.AllElementsCatalog;
import metaindex.data.catalog.CatalogHandle;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.catalog.TemplatesElementsCatalog;
import metaindex.data.dataset.IDataset;
import metaindex.data.element.ElementHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.Metadata;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDBAccessedData;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IGenericEncodable.UnableToPopulateException;
import metaindex.websockets.AMetaindexWSServer;
import metaindex.websockets.ThumbnailsServer;
import metaindex.websockets.ChatServer;
import metaindex.websockets.ElementDataServer;

/**
 * Bean storing community data
 * @author laurent
 *
 */
public class Community extends AMultiLanguageMetaindexData<ICommunity,CommunityVocabularySet> implements ICommunity {

	private static final int WS_CHAT_PORT=8887;
	private static final int WS_BASE_THUMBNAILS_PORT=15000;
	private static final int WS_ELEMENT_DATA_PORT=40000;
	private static final int MIN_CATALOG_ID=1000;
	private static final String DEFAULT_GROUP_NAME="Workers";
	
	private Log log = LogFactory.getLog(Community.class);
	private boolean issync=false;
	private int communityID;
	private String creatorName;	
	private int groupId;
	private String groupName="Workers";
	
	private static boolean showSetIdentifiedWarning = true;

	private Map<String, IUserProfileData> loggedUsers = new HashMap<String, IUserProfileData>(); 
	List<ICommunityTerm> terms=new ArrayList<ICommunityTerm>();
	List<CommunityDatatype> datatypes = new ArrayList<CommunityDatatype>();
	List<IElement> elements=new ArrayList<IElement>();
	Map<Integer,IElement> elementsMap=new HashMap<Integer,IElement>();
	List<ICatalog> catalogs =  new ArrayList<ICatalog>();
	Map<Integer,ICatalog> catalogsMap =new HashMap<Integer,ICatalog>();
	
	private ChatServer chatServer;
	private ThumbnailsServer thumbnailsServer;
	private ElementDataServer elementDataServer;
	

	@Override
	public void checkDataDBCompliance() throws DataAccessConstraintException {
		checkCompliantWithDBSmallString("field 'idName' of community '"+this.getIdName()+"'",this.getIdName());		
	}
	
	public Community(String myIdName) {
		
		super(CommunitiesAccessor.getDataAccessors());	
		this.setIdName(myIdName);		
	}
	@Override
	public void startServices() {

			log.error(this.getIdName()+ " : ### Starting services");
		// port = 0 --> choose a random free one
		try { chatServer=new ChatServer(this.getChatWSServerPort(),this); } 
		catch ( IOException | InterruptedException e) {
			log.error("Cannot start WS Chat Server : port "+WS_CHAT_PORT+" already in use.");
			//e.printStackTrace();
		}
		// port = 0 --> choose a random free one
		try { thumbnailsServer=new ThumbnailsServer(this.getCatalogsWSServerPort(),this); }
		catch ( IOException | InterruptedException e) {
			log.error("Cannot start WS Catalog Server : port "+this.getCatalogsWSServerPort()+" already in use.");
			//e.printStackTrace();
		}
		
		try { elementDataServer=new ElementDataServer(this.getElementDataWSServerPort(),this); }
		catch ( IOException | InterruptedException e) {
			log.error("Cannot start WS Catalog Server : port "+this.getElementDataWSServerPort()+" already in use.");
			//e.printStackTrace();
		}
		
	}
	@Override
	public void enter(IUserProfileData p) 
	{ 
		log.error("### user "+p.getEmail()+" ("+p+") enters "+this.getIdName());
		
		loggedUsers.put(p.getSessionId(), p);
		p.setSelectedCommunity(new CommunityHandle(p, this));		
		log.info("Community "+this.getIdName()+" : added user '"+p.getUsername()+"' sessionId="+p.getSessionId()
																		+" Total: "+loggedUsers.size()+" users");		
	}
	
	public Integer getChatWSServerPort() {
		//log.error("Community "+this.getIdName()+" ID = "+this.getCommunityId());
		return WS_CHAT_PORT+this.getCommunityId();
	}

	public Integer getCatalogsWSServerPort() {
		//log.error("Community "+this.getIdName()+" ID = "+this.getCommunityId());
		return WS_BASE_THUMBNAILS_PORT+this.getCommunityId();
	}

	public Integer getElementDataWSServerPort() {
		//log.error("Community "+this.getIdName()+" ID = "+this.getCommunityId());
		return WS_ELEMENT_DATA_PORT+this.getCommunityId();
	}
	
	@Override
	public void quit(IUserProfileData p) {
		if (loggedUsers.containsKey(p.getSessionId())) {
			log.error("### user "+p.getEmail()+" ("+p+") quits "+this.getIdName());
			loggedUsers.remove(p.getSessionId());
		}
	}
	
	@Override
	public IUserProfileData getLoggedUserProfile(String sessionId) { return loggedUsers.get(sessionId); }
	
	@Override
	public List<IElement> getTemplateElements() {
		return this.getCatalog(TemplatesElementsCatalog.TEMPLATES_ELEMENTS_CATALOG_ID).getElements();
	}
	
  	@Override
  	public boolean isIdentified() {
  		return this.getIdName().length()>0;
  	}
	
	@Override
	public Integer getCommunityId() {
		return communityID;
	}

	public void setCommunityID(int communityID) {
		this.communityID = communityID;
	}
	@Override
	public String getCreatorName() {
		return creatorName;
	}
	@Override
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	@Override
	public void addTerm(ICommunityTerm newTerm) {
		terms.add(newTerm);
	}
	@Override
	public List<ICommunityTerm> getTerms() {
		return terms;
	}
	@Override
	public List<IElement> getElements() {
		return elements;
	}
	@Override
	public List<IElementHandle> getUserElements(IUserProfileData user) {
		// TODO find a better way to do that : maybe result should be stored in the UserCommunityData object.
		List<IElementHandle> result = new ArrayList<IElementHandle>();
		Iterator<IElement> it = elements.iterator();
		while (it.hasNext()) { result.add(new ElementHandle(user,it.next())); }		
		return result;
	}
	
	@Override
	public Map<Integer,IElement> getElementsMap() {
		return elementsMap;
	}
	@Override
	public List<ICatalog> getCatalogs() {
		return catalogs;
	}

	@Override
	public ICatalog getCatalog(int catalogId) throws DataAccessErrorException {
		ICatalog result = catalogsMap.get(catalogId);
		if (result==null) { 
			throw new DataAccessErrorException("Catalog ID '"+catalogId+"' not found in community '"+this.getIdName()+"'");
		}		
		return result;		
	}
	@Override
	public ICatalogHandle getUserCatalog(IUserProfileData user,int catalogId) throws DataAccessErrorException {
		return new CatalogHandle(user,getCatalog(catalogId));		
	}	
	@Override
	public IElement getElement(Integer elementId) throws DataAccessErrorException {
		
		IElement result = elementsMap.get(elementId);		
		if (result==null) { 
			throw new DataAccessErrorException("Element ID '"+elementId+"' not found in community '"+this.getIdName()+"'");
		}
		
		return result;
	}
	public IDataset getDataset(Integer datasetId) throws DataAccessErrorException {
		Iterator<IElement> it = elements.iterator();
		while (it.hasNext()) {
			IElement el = it.next();
			try { return el.getDataset(datasetId); }
			catch (DataAccessErrorException e) { /* Dataset not found in this element */}
		}
		return null;
	}
	public IMetadata getMetadata(Integer metadataId) throws DataAccessErrorException {
		Iterator<IElement> it = elements.iterator();
		while (it.hasNext()) {
			IElement el = it.next();
			Iterator<IDataset> it2 = el.getDatasets().iterator();
			while (it2.hasNext()){
				IDataset d = it2.next();
				try { return d.getMetadata(metadataId); }
				catch (DataAccessErrorException e) { /* Dataset not found in this element */}
			}
		}
		return null;
	}
	@Override
	public IElementHandle getUserElement(IUserProfileData user,Integer elementId) throws DataAccessErrorException {		
		IElement el = getElement(elementId);		
		return new ElementHandle(user,el);
	}
	protected void setTerms(List<ICommunityTerm> terms) {
		this.terms = terms;
	}	
	@Override
	public ICommunity getCommunityData() { return this; }


	@Override
	public ICommunityTerm getTermData(String termIdName) {
		Iterator<ICommunityTerm> it = this.getTerms().iterator();
		while (it.hasNext()){
			ICommunityTerm cur = it.next();
			if (cur.getIdName().equals(termIdName) ) { return cur; }
		}
		log.error("Term Data '"+termIdName+"' not found in community "+this.getIdName());
		return null;
	}

	@Override
	public ICommunityTerm getTermDataById(int termId) {
		Iterator<ICommunityTerm> it = this.getTerms().iterator();
		while (it.hasNext()){
			ICommunityTerm cur = it.next();
			if (cur.getTermId()==termId) { return cur; }
		}
		log.error("Term Data id '"+termId+"' not found in community "+this.getIdName());
		return null;
	}
	


	
	@Override
	public void update(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException {
		
		getCommunityDBAccessor().refreshFromDB(activeUser, this);
		
		// we consider here that terms are part of the Core Community data
		// that's why we load it by default
		this.terms.clear();
				
		this.setTerms(this.getCommunityTermsDBAccessor().loadAssociatedData(activeUser, this));
		
	}
	
	@Override
	public void addElement(IUserProfileData activeUser, IElement el) throws DataAccessConstraintException {
		if (!el.getCommunityData().getCommunityId().equals(this.getCommunityId())) {
			throw new DataAccessConstraintException("Trying to add element '"+el.getElementId()+"' to community '"+this.getIdName()
							+"', while element is declared to belong to another community '"+el.getCommunityData().getIdName()+"'");
		}
		elements.add(el);
		elementsMap.put(el.getElementId(), el);
		/*
		Iterator<ICatalog> it = this.getCatalogs().iterator();
		while (it.hasNext()) {
			ICatalog c = it.next();
			try { c.updateFull(activeUser); } 
			catch (DataAccessErrorException | DataReferenceErrorException e) {
				e.printStackTrace();
				throw new DataAccessConstraintException(e);				
			}
		}*/
	}
	
	@Override
	public void createFullAllElements(IUserProfileData activeUser, List<IElement> elts) 
			throws DataAccessConstraintException, DataReferenceErrorException {
		
		this.getElementDBAccessor().createFullIntoDB(activeUser, elts);	
		this.loadCatalogsDataFromDB(activeUser);
	}
	
	@Override
	public void deleteElements(IUserProfileData activeUser, List<IElement> elts) 
			throws DataAccessConstraintException, DataAccessErrorException, DataReferenceErrorException {
		
		log.info("Performing DB deletion of "+elts.size()+" elements");
		this.getElementDBAccessor().deleteFromDB(activeUser, elts);
		
		log.info("Performing community deletion of "+elts.size()+" elements");
		Iterator<IElement> itEl = elts.iterator();
		while (itEl.hasNext()) {
			IElement curEl = itEl.next(); 
			this.removeElement(activeUser, this.getElement(curEl.getElementId()));
		}	
		
		// remove elts from non-virtual catalogs
		// (which by definition don't have any static element)
		Iterator<ICatalog> it = this.getCatalogs().iterator();
		while (it.hasNext()) {
			ICatalog c = it.next();
			log.info("Performing deletion of "+elts.size()+" elements from catalog "+c.getName());
			if (!c.isVirtual()) {
				c.removeStaticElements(activeUser,elts);								
			}
		}
		
	}
	
	@Override
	public void removeElement(IUserProfileData activeUser, IElement el) throws DataAccessConstraintException {
		
		if (!el.getCommunityData().getCommunityId().equals(this.getCommunityId())) {
			throw new DataAccessConstraintException("Trying to remove element '"+el.getElementId()+"' to community '"+this.getIdName()
							+"', while element is declared to belong to another community '"+el.getCommunityData().getIdName()+"'");
		}
		try { this.getElement(el.getElementId()); }
		catch(Exception DataAccessErrorException) {
			throw new DataAccessConstraintException("Trying to remove element '"+el.getElementId()+"' to community '"+this.getIdName()
							+"', while element is not present in it.");
		}
		
		//remove element from any catalog it belongs to
		elements.remove(elements.indexOf(el));
		elementsMap.remove(el.getElementId());
		el.setOutDated();
		
	}
	
	@Override
	public void addCatalog(ICatalog cat) throws DataAccessConstraintException {
		if (!cat.getCommunityData().getCommunityId().equals(this.getCommunityId())) {
			throw new DataAccessConstraintException("Trying to add element '"+cat.getCatalogId()+"' to community '"+this.getIdName()
							+"', while element is declared to belong to another community '"+cat.getCommunityData().getIdName()+"'");
		}
		catalogs.add(cat);
		catalogsMap.put(cat.getCatalogId(), cat);
	}	
		
	/**
	 * Refresh full list of elements contained by this community.
	 * @param activeUser
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 * @throws DataReferenceErrorException
	 */
	public void loadElementsDataFromDB(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException,DataReferenceErrorException {
		
		List<IElement> elementsToRemove = new ArrayList<IElement>();
		
		// retrieve updated Elements list
		// new elements have already been added to the community
		// but we still have to track the ones to be removed
		List<IElement> refreshedElements=this.getElementDBAccessor().loadAssociatedData(activeUser, this);
		
		// detect removed elements comparing the list we got from DB update and current list
		Iterator<IElement> itEl = elements.iterator();
		while (itEl.hasNext()) {
			IElement curEl = itEl.next();
			if (!refreshedElements.contains(curEl)) { elementsToRemove.add(curEl); }			
		}
		
		// remove those detected elements which have disappeared
		Iterator<IElement> itElToRemove = elementsToRemove.iterator();
		while (itElToRemove.hasNext()) { this.removeElement(activeUser,itElToRemove.next()); }
				
	}
	
	/**
	 * TODO throw exception when error while loading catalog contents
	 * @param activeUser
	 * @throws DataReferenceErrorException 
	 * @throws DataAccessConstraintException 
	 */
	public void loadCatalogsDataFromDB(IUserProfileData activeUser) throws DataAccessConstraintException, DataReferenceErrorException {
		
		// list of new catalogs
		// they have already been added to the community catalogs list
		// but we still have to remove the ones who disappeared
		List<ICatalog> newCatalogsList = this.getCatalogDBAccessor().loadAssociatedData(activeUser, this); 				

		// creating default (called virtual) catalogs if not yet in the catalogs list
		if (catalogsMap.get(AllElementsCatalog.ALL_ELEMENTS_CATALOG_ID)==null) {
			// adding virtual 'all elements' catalog			
			ICatalog allElementsCatalog = new AllElementsCatalog(this);
			allElementsCatalog.setName(activeUser.getText("catalog.allelements.name"));
			allElementsCatalog.setComment(activeUser.getText("catalog.allelements.comment"));
			allElementsCatalog.setCommunityId(this.getCommunityId());			
			addCatalog(allElementsCatalog);
			
			// adding virtual 'templates' ('bases') catalog			
			ICatalog templatesCatalog = new TemplatesElementsCatalog(this);
			templatesCatalog.setName(activeUser.getText("catalog.templates.name"));
			templatesCatalog.setComment(activeUser.getText("catalog.templates.comment"));
			templatesCatalog.setCommunityId(this.getCommunityId());
			addCatalog(templatesCatalog);		
			
		}
		
		// Remove user Catalogs which does not exist anymore in DB
		Iterator<ICatalog> itCurrentCatalogs = catalogs.iterator();
		List<ICatalog> catalogsToRemove = new ArrayList<ICatalog>();
		while (itCurrentCatalogs.hasNext()) {
			ICatalog cur = itCurrentCatalogs.next();
			// Virtual catalogs shall not be removed, this operation should only occure
			// for 'users' catalogs.
			if (cur.getCatalogId()>=MIN_CATALOG_ID && newCatalogsList.indexOf(cur)==-1) {
				catalogsToRemove.add(cur);				
			}
		}

		Iterator<ICatalog> itCatalogsToRemove = catalogsToRemove.iterator();
		while(itCatalogsToRemove.hasNext()) {
			ICatalog cur = itCatalogsToRemove.next();
			catalogs.remove(cur);
			catalogsMap.remove(cur.getCatalogId());
		}
		
		// update catalogs contents
		Iterator<ICatalog> itCatalogs = catalogs.iterator();
		while (itCatalogs.hasNext()) {
			ICatalog curCatalog = itCatalogs.next();
			curCatalog.updateFull(activeUser); 
		}
		
		// update current user selected catalog
		// TODO: move this code into UserProfileData
		if (activeUser.getSelectedCommunity()==null) { return; }
		// refresh the 'selectedCatalog' 
		// TODO find a better place to do it
		if (activeUser.getSelectedCommunity().getSelectedCatalog()==null)
		{ 
			activeUser.getSelectedCommunity().setSelectedCatalog(new CatalogHandle(activeUser,
									catalogsMap.get(AllElementsCatalog.ALL_ELEMENTS_CATALOG_ID))); 
		}
		else {
			int selectedCatalogId=activeUser.getSelectedCommunity().getSelectedCatalog().getCatalogId();
			// if previously selected catalog still exist, we reselect it, otherwise select the "All" catalog instead
			try {
				ICatalogHandle catalogToSelect=this.getUserCatalog(activeUser,selectedCatalogId);
				activeUser.getSelectedCommunity().setSelectedCatalog(catalogToSelect);					
			} catch (DataAccessErrorException e) {
				// the previously selected catalog does not exist anymore
				// so we select the 'All' catalog by default
				activeUser.getSelectedCommunity().setSelectedCatalog(new CatalogHandle(activeUser,catalogsMap.get(0)));
			}				
		}	
		
	}
	
	@Override
	public void commit(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException {
			getCommunityDBAccessor().storeIntoDB(activeUser, this);
	}
	
	@Override
	public void create(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		this.setCreatorName(activeUser.getUsername());
		this.getCommunityDBAccessor().createIntoDB(activeUser, this);
		this.update(activeUser);
	}

	@Override
	public void delete(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		this.getCommunityDBAccessor().deleteFromDB(activeUser, this);
		
	}
	
	@Override
	public int getGroupId(){
		return groupId;
	}
	
	public void setGroupId(int groupId) {
		this.groupId=groupId;
	}
	@Override
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName=groupName;
		
	}
	
	@Override
	public CommunityVocabularySet getVocabulary(IUserProfileData activeUser) {
		
		Iterator<CommunityVocabularySet> it = this.getVocabularySets().iterator();
		while (it.hasNext()) {
			CommunityVocabularySet cur=it.next();
			if (cur.getGuiLanguageID()==activeUser.getGuiLanguageId()) { return cur; }
		}
		return null;
	}
	
	public void joinCommunity(IUserProfileData  activeUser, IUserProfileData profile) throws DataAccessErrorException, DataAccessConstraintException 
	{
		
		List<ICommunityHandle> currentCommunities = activeUser.getUserCommunities(profile);
		Iterator<ICommunityHandle> it = currentCommunities.iterator();
		while (it.hasNext())
		{
			ICommunityHandle cur = it.next();
			//check if user already in community, then no need to join it again and return silently
			if (cur.getIdName().equals(this.getIdName())) {	return; }
		}
		
		this.getCommunityDBAccessor().addAssociation(activeUser, this, profile);	
	}

	@Override
	public int getNbElements() {
		return this.getElements().size();
	}

	@Override
	public List<CommunityDatatype> getDatatypes() {
		return datatypes;
	}

	@Override
	public String getDatatypeName(int datatypeId) {
		Iterator<CommunityDatatype> it = this.getDatatypes().iterator();
		while (it.hasNext()) {
			CommunityDatatype d = it.next();
			if (d.getDatatypeId()==datatypeId) { return d.getDatatypeName(); }
		}
		
		return "";
	}

	@Override
	public void setDatatypes(List<CommunityDatatype> datatypes) {
		this.datatypes = datatypes;
	}


	@Override
	public boolean isReadOnly() {
		// a community is never read-only
		return false;
	}

	@Override
	public void setReadOnly(boolean isReadOnly) {
		// do nothing		
		log.warn("Trying to set read-only flag of a community, which is for now always writable");
	}

	@Override
	public void invalidate() {
		issync=false;
		
	}

	@Override
	public void commitFull(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		this.commit(activeUser);
		
		Iterator<ICatalog> itcat = this.getCatalogs().iterator();
		while (itcat.hasNext()) { itcat.next().commit(activeUser); }
		
		Iterator<IElement> it = this.getElements().iterator();
		while (it.hasNext()) { it.next().commitFull(activeUser); }
				
	}

	@Override
	public void updateFull(IUserProfileData activeUser)
			throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
		
		this.update(activeUser);
		this.loadElementsDataFromDB(activeUser);
		this.loadCatalogsDataFromDB(activeUser);
	}


	@Override
	public boolean isSynchronized() {		
		return issync;
	}

	@Override
	public ICommunity clone() throws CloneNotSupportedException {
		log.error("clone not implemented yet for communityData");		
		return null;
	}


	@Override
	public boolean isModifyOverridenTemplate() {
		return false;
	}

	@Override
	public boolean isTemplated() {
		return false;
	}

	@Override
	public List<Integer> getElementsIdsByTerm(Integer termId) {
		return this.getElementDBAccessor().getElementsByTerm(termId);
	}

	/**
	 * @return the chatServer
	 */
	@Override
	public ChatServer getChatServer() {
		return chatServer;
	}

	
	/**
	 * @return the catalogServer
	 */
	@Override
	public ThumbnailsServer getCatalogServer() {
		return thumbnailsServer;
	}	
	/**
	 * @return the elementDataServer
	 */
	@Override	
	public AMetaindexWSServer getElementDataServer() {
		return elementDataServer;
	}	

	@Override
	public void populateFromJson(IUserProfileData activeUser, JSONObject json, POPULATE_POLICY policy) throws UnableToPopulateException {
		this.populateFromJson(null, json, policy);
		this.commit(activeUser);
		
	}
}
