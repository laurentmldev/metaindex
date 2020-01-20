package metaindex.websockets.catalogs;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import metaindex.websockets.commons.IWsMsg_answer;

public class WsMsgCustomizeCatalog_answer extends WsMsgCustomizeCatalog_request implements IWsMsg_answer  {

	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }

	public WsMsgCustomizeCatalog_answer(WsMsgCustomizeCatalog_request request) {
		this.setRequestId(request.getRequestId());
		this.setId(request.getId());// the catalog id
		this.setName(request.getName()); // the catalog name
		this.setItemNameFields(request.getItemNameFields());
		this.setItemsUrlPrefix(request.getItemsUrlPrefix());
		this.setItemThumbnailUrlField(request.getItemThumbnailUrlField());
		this.setThumbnailUrl(request.getThumbnailUrl());	
		this.setPerspectiveMatchField(request.getPerspectiveMatchField());
	}

}
