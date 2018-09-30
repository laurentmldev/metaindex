package metaindex.data.community;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.AAccessControledData;
import metaindex.data.IBufferizedData;
import metaindex.data.catalog.CatalogHandle;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.catalog.TemplatesElementsCatalog;
import metaindex.data.element.Element;
import metaindex.data.element.ElementHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;
import metaindex.websockets.AMetaindexWSServer;

/**
 * Bean storing community data
 * @author laurent
 *
 */
public class CommunityHandle extends AAccessControledData<ICommunity> implements ICommunityHandle  {

	private Log log = LogFactory.getLog(CommunityHandle.class);
	
	private boolean issynchronized=false;
	//Map<Integer,ICatalogHandle> bufCatalogs = new HashMap<Integer,ICatalogHandle>();
	
	private ICatalogHandle selectedCatalog=null;
	
	private Map<Integer,Integer> catalogsSelectedElements=new HashMap<Integer,Integer>();
	
	public CommunityHandle(IUserProfileData refUserProfile, ICommunity refCommunity) {
		super(refUserProfile,refCommunity);
	}
	
	@Override
	public void invalidate() {
		issynchronized=false;
		//bufCommunityElements.clear();	
		//bufCatalogs.clear();
		
	}
 
	@Override
	public boolean isSynchronized() {
		return issynchronized;
	}
	@Override	
	public void createFullElements(IUserProfileData activeUser, List<IElement> elts) 
						throws DataAccessConstraintException, DataReferenceErrorException {
		this.getRefData().createFullAllElements(activeUser, elts);
	}
	@Override
	public void deleteElements(IUserProfileData activeUser, List<IElementHandle> elts) 
						throws DataAccessConstraintException, DataAccessErrorException, DataReferenceErrorException {
		List<IElement> myElts = new ArrayList<IElement>();
		
		// have to go through all elementsHandle to get Elements list
		// not so optimized ...
		Iterator<IElementHandle> it = elts.iterator();
		while (it.hasNext()) { myElts.add(this.getRefData().getElement(it.next().getElementId())); }
		this.getRefData().deleteElements(activeUser, myElts);
	}
	
	@Override
	public IUserProfileData getLoggedUserProfile(String sessionId) {
		return this.getRefData().getLoggedUserProfile(sessionId);
	}
	
	@Override
	public Integer getCommunityId() {
		this.checkReadableByUser();
		return this.getRefData().getCommunityId();
	}
	@Override
	public List<IElementHandle> getTemplateElements() {
		return this.getCatalog(TemplatesElementsCatalog.TEMPLATES_ELEMENTS_CATALOG_ID).getElements();
		
	}		
	
	@Override
	public String getCreatorName() {
		this.checkReadableByUser();
		return getRefData().getCreatorName();
	}
	@Override
	public List<ICommunityTermHandle> getTerms() {
		this.checkReadableByUser();		
		List<ICommunityTermHandle> result = new ArrayList<ICommunityTermHandle>();
		Iterator<ICommunityTerm> it = getRefData().getTerms().iterator();
		while (it.hasNext()) {
			result.add(new CommunityTermHandle(this.getUserProfile(),it.next()));
		}
		return result;				
	}
	@Override
	public List<IElementHandle> getElements() {
		this.checkReadableByUser();
		return getRefData().getUserElements(this.getUserProfile());
	}
	
	@Override
	public List<ICatalogHandle> getCatalogs() {
		this.checkReadableByUser();
		List<ICatalogHandle> result = new ArrayList<ICatalogHandle>();
		Iterator<ICatalog> it = this.getRefData().getCatalogs().iterator();
		while (it.hasNext()) { result.add(new CatalogHandle(this.getUserProfile(),it.next())); }
		
		return result;
	}
	
	@Override
	public ICatalogHandle getCatalog(int catalogId) throws DataAccessErrorException {
		this.checkReadableByUser();
		return getRefData().getUserCatalog(this.getUserProfile(),catalogId);//bufCatalogs.get(catalogId);
	}
	@Override
	public IElementHandle getElement(Integer elementId) throws DataAccessErrorException {
		this.checkReadableByUser();
		return getRefData().getUserElement(this.getUserProfile(),elementId); //bufCommunityElements.get(elementId);
	}
	@Override
	public ICommunityTermHandle getTermData(String termIdName) {
		this.checkReadableByUser();		
		ICommunityTermData data = getRefData().getTermData(termIdName);
		if (data==null) { return null; }
		else { return new CommunityTermHandle(this.getUserProfile(),getRefData().getTermData(termIdName)); }		
	}
	
	@Override
	public ICommunityTermHandle getTermDataById(int termId) {
		this.checkReadableByUser();
		ICommunityTermData data = getRefData().getTermDataById(termId);
		if (data==null) { return null; }
		else { return new CommunityTermHandle(this.getUserProfile(),getRefData().getTermDataById(termId)); }
	}

	// TODO make it static
	@Override
	public List<ICommunityHandle> getCommunities() 
				throws DataAccessErrorException, DataAccessConstraintException {		
		return this.getUserProfile().getUserCommunities(getUserProfile());		
	}

	


	@Override
	public int getGroupId(){
		this.checkReadableByUser();
		return getRefData().getGroupId();
	}
	
	@Override
	public String getGroupName() {
		this.checkReadableByUser();
		return getRefData().getGroupName();
	}
	@Override
	public CommunityVocabularySet getVocabulary() {
		this.checkReadableByUser();
		return getRefData().getVocabulary(this.getUserProfile());
	}

	@Override
	public void joinCommunity() throws DataAccessErrorException, DataAccessConstraintException 
	{
		this.checkManageableByUser();
		getRefData().joinCommunity(this.getUserProfile(), this.getUserProfile());
	}
	@Override
	public ICatalogHandle getSelectedCatalog() {
		this.checkReadableByUser();
		return selectedCatalog;
	}
	@Override
	public void setSelectedCatalog(ICatalogHandle selectedCatalog) 
			throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
		this.checkWritableByUser();
		
		if (this.selectedCatalog!=null && this.selectedCatalog.getSelectedElement()!=null) {
			catalogsSelectedElements.put(this.selectedCatalog.getCatalogId(), this.selectedCatalog.getSelectedElement().getElementId());
		}
		this.selectedCatalog = new CatalogHandle(this.getUserProfile(),this.getRefData().getCatalog(selectedCatalog.getCatalogId()));
		if (catalogsSelectedElements.get(this.selectedCatalog.getCatalogId())!=null) {
			int previouslySelectedElementId = catalogsSelectedElements.get(this.selectedCatalog.getCatalogId());
			try { 
				// check if element is reachable
				IElementHandle el = this.getElement(previouslySelectedElementId); 
				if (!el.isOutDated()) { this.selectedCatalog.setSelectedElement(previouslySelectedElementId); }
			}
			catch (DataAccessErrorException e) {
				// Element is not reachable anymore (probably removed by user)
				this.selectedCatalog.setSelectedElement(0);				
			}
			
		} 
		if (this.selectedCatalog.getSelectedElement()==null) { this.selectedCatalog.nextElement(); }
	}

	@Override
	public String getIdName() {
		this.checkReadableByUser();
		return getRefData().getIdName();
	}

	@Override
	public boolean isIdentified() {
		this.checkReadableByUser();
		return this.getRefData().isIdentified();
	}

	  /**
	   * Get the currently selected element of the currently selected catalog.
	   * @return
	   */
	@Override
	public IElementHandle getSelectedElement() {
		this.checkReadableByUser();
		return getSelectedCatalog().getSelectedElement();
	}


	@Override
	public List<CommunityVocabularySet> getVocabularySets() {
		this.checkReadableByUser();
		return this.getRefData().getVocabularySets();
	}


	@Override
	public void setVocabularySets(List<CommunityVocabularySet> vocabularySets) {
		this.checkWritableByUser();
		this.getRefData().setVocabularySets(vocabularySets);		
	}


	@Override
	public void addVocabularySet(CommunityVocabularySet vocabularySet) {
		this.checkWritableByUser();
		this.addVocabularySet(vocabularySet);
	}


	@Override
	public void setCommunityID(int communityID) {
		this.checkManageableByUser();
		this.getRefData().setCommunityID(communityID);		
	}


	@Override
	public void setIdName(String idName) {
		this.checkManageableByUser();
		this.getRefData().setIdName(idName);
	}


	@Override
	public void setCreatorName(String creatorName) {
		this.checkManageableByUser();
		this.getRefData().setCreatorName(creatorName);
	}


	@Override
	public void setGroupId(int groupId) {
		this.checkManageableByUser();
		this.getRefData().setGroupId(groupId);
	}


	@Override
	public void setGroupName(String groupName) {
		this.checkManageableByUser();
		this.getRefData().setGroupName(groupName);
	}

	@Override
	public int getNbElements() {
		this.checkReadableByUser();
		return this.getRefData().getNbElements();
	}

	@Override
	public List<CommunityDatatype> getDatatypes() {
		return this.getRefData().getDatatypes();
	}

	@Override
	public void setDatatypes(List<CommunityDatatype> datatypes) {
		// maybe remove it from the interface?
		this.checkWritableByUser();
		this.getRefData().setDatatypes(datatypes);		
	}

	@Override
	public String getDatatypeName(int datatypeId) {
		this.checkReadableByUser();
		return this.getRefData().getDatatypeName(datatypeId);
	}

	@Override
	public void addTerm(ICommunityTermHandle newTerm) throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
		ICommunityTerm t = this.getRefData().getTermData(newTerm.getIdName());
		this.getRefData().addTerm(t);		
	}

	@Override
	public Map<Integer, IElementHandle> getElementsMap() {
		Map<Integer,IElementHandle> result = new HashMap<Integer,IElementHandle>(); 
		Map<Integer,IElement> elsMap = this.getRefData().getElementsMap();
		Iterator<IElement> it = elsMap.values().iterator();
		while (it.hasNext()) {
			IElement e = it.next();
			result.put(e.getElementId(),new ElementHandle(this.getUserProfile(),e));
		}
		return result;
	}

	@Override
	public void joinCommunity(IUserProfileData userToAdd)
			throws DataAccessErrorException, DataAccessConstraintException {
		this.getRefData().joinCommunity(this.getUserProfile(), userToAdd);
		
	}
	
	@Override
	public Map<String, ICommunityTermHandle> getTermsMap() {
		Map<String, ICommunityTermHandle> result = new HashMap<String, ICommunityTermHandle>();
		Iterator<ICommunityTerm> it = this.getRefData().getTerms().iterator();
		while (it.hasNext()) {
			ICommunityTerm curTerm = it.next();
			result.put(curTerm.getIdName(),new CommunityTermHandle(this.getUserProfile(),curTerm));			
		}
		return result;
	}

	@Override
	public List<Integer> getElementsByTerm(Integer termId) {
		return this.getRefData().getElementsIdsByTerm(termId);
	}

	@Override
	public void enter() {
		this.getRefData().enter(this.getUserProfile());		
	}


	@Override
	public void quit() {
		this.getRefData().quit(this.getUserProfile());
	}

	@Override
	public AMetaindexWSServer getCatalogServer() {
		return this.getRefData().getCatalogServer();
	}

	@Override
	public AMetaindexWSServer getElementDataServer() {
		return this.getRefData().getElementDataServer();
	}

	@Override
	public AMetaindexWSServer getChatServer() {
		return this.getRefData().getChatServer();
	}
	
	@Override
	public void checkDataDBCompliance() throws DataAccessConstraintException {
		this.getRefData().checkDataDBCompliance();		
	}

}
