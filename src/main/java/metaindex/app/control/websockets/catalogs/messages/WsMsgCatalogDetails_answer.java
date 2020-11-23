package metaindex.app.control.websockets.catalogs.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import metaindex.data.filter.IFilter;
import metaindex.app.Globals;
import metaindex.app.control.websockets.commons.IWsMsg_answer;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.CatalogVocabularySet;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogCustomParams;
import metaindex.data.perspective.ICatalogPerspective;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.userprofile.ICatalogUser;
import metaindex.data.userprofile.ICatalogUser.USER_CATALOG_ACCESSRIGHTS;
import metaindex.data.userprofile.IUserProfileData;

public class WsMsgCatalogDetails_answer implements IWsMsg_answer,ICatalogCustomParams  {
		
	private Integer _requestId=0;
	private Integer _id;
	private String _name;
	private String _ownerName;
	private Integer _ownerId;
	private String _planName;
	private Integer _planId;
	private Boolean _isUserCurrentCatalog=false;
	private Boolean _isEnabled=false;
	private String _itemThumbnailUrlField;
	private List<String> _itemNameFields;
	private String _thumbnailUrl;
	private String _itemsUrlPrefix;
	private String _pespectiveMatchField;
	private Long _quotaNbDocs=Catalog.DEFAULT_QUOTA_NBDOCS;
	private Long _quotaDiscSpaceBytes=Catalog.DEFAULT_QUOTA_DISCSPACEBYTES;
	private Long _nbDocuments;
	private Long _discSpaceUseBytes=Long.MAX_VALUE;
	private Boolean _dbIndexFound;
	private Integer _ftpPort=0;
	private Integer _timeFieldTermId=null;
	
	private Map<String,ICatalogTerm> _terms = new HashMap<>();
	private Map<String,ICatalogPerspective> _perspectives;
	private List<IFilter> _filters = new ArrayList<>();
	private Map<String,CatalogVocabularySet> _vocabularies = new HashMap<>();
	private CatalogVocabularySet _vocabulary = new CatalogVocabularySet();
	private USER_CATALOG_ACCESSRIGHTS _userAccessRights=USER_CATALOG_ACCESSRIGHTS.NONE;
	private String _userAccessRightsStr="";
	
	private Boolean _isSuccess=true;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }

	public WsMsgCatalogDetails_answer() {};
	public WsMsgCatalogDetails_answer(ICatalog c, IUserProfileData u) {
		IUserProfileData owner = Globals.Get().getUsersMgr().getUserById(c.getOwnerId());
		this.setId(c.getId());
		this.setName(c.getName());	
		this.setOwnerName(owner.getNickname());
		this.setOwnerId(owner.getId());
		this.setPlanName(owner.getPlan().getName());
		this.setPlanId(owner.getPlan().getId());
		this.setThumbnailUrl(c.getThumbnailUrl());
		this.setNbDocuments(c.getNbDocuments());
		this.setItemNameFields(c.getItemNameFields());
		this.setItemThumbnailUrlField(c.getItemThumbnailUrlField());
		this.setItemsUrlPrefix(c.getItemsUrlPrefix());
		this.setPerspectiveMatchField(c.getPerspectiveMatchField());		
		this.setPerspectives(c.getPerspectives());	
		this.setFilters(c.getFilters());
		this.setIsDbIndexFound(c.isDbIndexFound());
		this.setVocabularies(c.getVocabularies());
		this.setVocabulary(c.getVocabulary(u.getGuiLanguageId()));
		this.setFtpPort(c.getDrivePort());
		this.setUserAccessRights(u.getUserCatalogAccessRights(c.getId()));
		this.setUserAccessRightsStr(u.getText("Profile.userAccessRights."+this.getUserAccessRights().toString()));
		this.setQuotaNbDocs(c.getQuotaNbDocs());
		this.setQuotaFtpDiscSpaceBytes(c.getQuotaFtpDiscSpaceBytes());
		this.setDiscSpaceUseBytes(c.getDiscSpaceUseBytes());
		this.setTimeFieldTermId(c.getTimeFieldTermId());
		this.setEnabled(c.isEnabled());
		if (u.getCurrentCatalog()==c) { this.setIsUserCurrentCatalog(true); }	
		
		// ignore terms for MX internal use (which start with "mx_")
		for (ICatalogTerm catalogTerm : c.getTerms().values()) {
			if (catalogTerm.getName().startsWith("mx_")) { continue; }
			_terms.put(catalogTerm.getName(), catalogTerm);
		}		
		
	}	
	@Override
	public Integer getId() {
		return _id;
	}
	
	public void setId(Integer id) {
		this._id = id;
	}
	@Override
	public String getName() {
		return _name;
	}
	@Override
	public void setName(String name) {
		this._name = name;
	}
	@Override
	public void setItemThumbnailUrlField(String thumbnailUrl) {
		_itemThumbnailUrlField=thumbnailUrl;
		
	}
	@Override
	public String getItemThumbnailUrlField() {
		return _itemThumbnailUrlField;
	}

	public Long getNbDocuments() {
		return _nbDocuments;
	}

	public void setNbDocuments(Long _nbDocuments) {
		this._nbDocuments = _nbDocuments;
	}
	@Override
	public List<String> getItemNameFields() {
		return _itemNameFields;
	}
	@Override
	public void setItemNameFields(List<String> itemsNameFields) {
		this._itemNameFields = itemsNameFields;
	}
	
	@Override
	public String getItemsUrlPrefix() {
		return _itemsUrlPrefix;
	}
	@Override
	public void setItemsUrlPrefix(String urlPrefix) {
		this._itemsUrlPrefix = urlPrefix;
	}
	@Override
	public String getThumbnailUrl() {
		return _thumbnailUrl;
	}
	@Override
	public void setThumbnailUrl(String url) {
		_thumbnailUrl=url;
		
	}
	public Map<String,ICatalogTerm> getTerms() {
		return _terms;
	}
	public void setTerms(Map<String,ICatalogTerm> terms) {
		this._terms = terms;
	}
	public List<IFilter> getFilters() {
		return _filters;
	}
	public void setFilters(List<IFilter> filters) {
		this._filters = filters;
	}
	public Boolean getIsDbIndexFound() {
		return _dbIndexFound;
	}
	public void setIsDbIndexFound(Boolean _dbIndexFound) {
		this._dbIndexFound = _dbIndexFound;
	}
	
	public Map<String,ICatalogPerspective> getPerspectives() {
		return _perspectives;
	}
	public void setPerspectives(Map<String,ICatalogPerspective> perspectives) {
		this._perspectives=perspectives;		
	}
	public Boolean getIsUserCurrentCatalog() {
		return _isUserCurrentCatalog;
	}
	public void setIsUserCurrentCatalog(Boolean isUserCurrentCatalog) {
		this._isUserCurrentCatalog = isUserCurrentCatalog;
	}
	public Map<String,CatalogVocabularySet> getVocabularies() {
		return _vocabularies;
	}
	public void setVocabularies(Map<String,CatalogVocabularySet> _vocabularies) {
		this._vocabularies = _vocabularies;
	}
	public CatalogVocabularySet getVocabulary() {
		return _vocabulary;
	}
	public void setVocabulary(CatalogVocabularySet vocabulary) {
		this._vocabulary = vocabulary;
	}
	public Integer getDrivePort() {
		return _ftpPort;
	}
	public void setFtpPort(Integer ftpPort) {
		this._ftpPort = ftpPort;
	}
	public USER_CATALOG_ACCESSRIGHTS getUserAccessRights() {
		return _userAccessRights;
	}
	public void setUserAccessRights(USER_CATALOG_ACCESSRIGHTS _userAccessRights) {
		this._userAccessRights = _userAccessRights;
	}
	public String getUserAccessRightsStr() {
		return _userAccessRightsStr;
	}
	public void setUserAccessRightsStr(String userAccessRightsStr) {
		_userAccessRightsStr=userAccessRightsStr;
	}
	public String getPerspectiveMatchField() {
		return _pespectiveMatchField;
	}
	public void setPerspectiveMatchField(String pespectiveMatchField) {
		this._pespectiveMatchField = pespectiveMatchField;
	}

	public Long getQuotaNbDocs() {
		return this._quotaNbDocs;
	}
	public void setQuotaNbDocs(Long maxNbDocs) {
		this._quotaNbDocs=maxNbDocs;
		
	}
	public Long getQuotaFtpDiscSpaceBytes() {
		return this._quotaDiscSpaceBytes;
	}
	public void setQuotaFtpDiscSpaceBytes(Long maxFtpSpaceBytes) {
		this._quotaDiscSpaceBytes=maxFtpSpaceBytes;		
	}
	public Long getDiscSpaceUseBytes() {
		return _discSpaceUseBytes;
	}
	public void setDiscSpaceUseBytes(Long discSpaceUseBytes) {
		this._discSpaceUseBytes = discSpaceUseBytes;
	}
	
	@Override
	public Integer getTimeFieldTermId() {
		return _timeFieldTermId;
	}
	@Override
	public void setTimeFieldTermId(Integer t) {
		_timeFieldTermId=t;
		
	}
	public String getOwnerName() {
		return _ownerName;
	}
	public void setOwnerName(String _ownerName) {
		this._ownerName = _ownerName;
	}
	public String getPlanName() {
		return _planName;
	}
	public void setPlanName(String _planName) {
		this._planName = _planName;
	}
	public Integer getOwnerId() {
		return _ownerId;
	}
	public void setOwnerId(Integer _ownerId) {
		this._ownerId = _ownerId;
	}
	public Integer getPlanId() {
		return _planId;
	}
	public void setPlanId(Integer _planId) {
		this._planId = _planId;
	}
	public Boolean getEnabled() {
		return _isEnabled;
	}
	public void setEnabled(Boolean _isEnabled) {
		this._isEnabled = _isEnabled;
	}
	public Integer getRequestId() {
		return _requestId;
	}
	public void setRequestId(Integer _requestId) {
		this._requestId = _requestId;
	}	
}
// catalog users are retrieved separatly from catalog itself,
// because it requires more complex operations and potentially
// several DB accesses