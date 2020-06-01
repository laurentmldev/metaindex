package metaindex.app.control.websockets.catalogs.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.List;

import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalogCustomParams;

public class WsMsgCustomizeCatalog_request implements ICatalogCustomParams {
	
	private Integer _requestId;
	private String _catalogName;
	private Integer _catalogId;
	private String _thumbnailUrl;
	private String _itemsUrlPrefix;
	private String _perspectiveMatchField;
	private List<String> _itemNameFields=new ArrayList<>();
	private String _itemThumbnailUrlField;
	private Integer _ftpPort=0;
	
	public WsMsgCustomizeCatalog_request() {
	
	}

		@Override
	public Integer getId() {
		return _catalogId;
	}
	public void setId(Integer id) {
		 _catalogId=id;
	}

	@Override
	public String getName() {
		return _catalogName;
	}

	@Override
	public void setName(String shortname) {
		_catalogName=shortname;		
	}

	@Override
	public List<String> getItemNameFields() {
		return _itemNameFields;
	}

	@Override
	public void setItemNameFields(List<String> fieldnames) {
		_itemNameFields=fieldnames;		
	}

	@Override
	public String getItemThumbnailUrlField() {
		return _itemThumbnailUrlField;
	}

	@Override
	public void setItemThumbnailUrlField(String fieldname) {
		_itemThumbnailUrlField=fieldname;
		
	}

	@Override
	public String getItemsUrlPrefix() {
		return _itemsUrlPrefix;
	}

	@Override
	public void setItemsUrlPrefix(String urlPrefix) {
		_itemsUrlPrefix=urlPrefix;		
	}

	@Override
	public String getThumbnailUrl() {
		return _thumbnailUrl;
	}

	@Override
	public void setThumbnailUrl(String thumbnailUrl) {
		_thumbnailUrl=thumbnailUrl;
		
	}

	public Integer getRequestId() {
		return _requestId;
	}

	public void setRequestId(Integer _requestId) {
		this._requestId = _requestId;
	}

	@Override
	public String getPerspectiveMatchField() {
		return _perspectiveMatchField;
	}

	@Override
	public void setPerspectiveMatchField(String fieldName) {
		_perspectiveMatchField=fieldName;		
	}

	@Override
	public Integer getFtpPort() {
		return _ftpPort;
	}
	public void setFtpPort(Integer p) {
		_ftpPort=p;
	}

}
