package metaindex.app.control.websockets.terms;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.List;

public class WsMsgUpdateTerm_request  {
		
	private Integer _requestId=0;
	private Integer _catalogId=0;
	private String _termName="";
	private String _termType="";
	private List<String> _termEnumsList=new ArrayList<>();
	private Boolean _isMultiEnum=false;
	
	public WsMsgUpdateTerm_request() {
	
	}


	public Integer getRequestId() {
		return _requestId;
	}


	public void setRequestId(Integer requestId) {
		this._requestId = requestId;
	}

	public String getTermName() {
		return _termName;
	}


	public void setTermName(String TermName) {
		this._termName = TermName;
	}


	public String getTermType() {
		return _termType;
	}


	public void setTermType(String termType) {
		this._termType = termType;
	}


	public Integer getCatalogId() {
		return _catalogId;
	}


	public void setCatalogId(Integer catalogId) {
		this._catalogId = catalogId;
	}


	public List<String> getTermEnumsList() {
		return _termEnumsList;
	}


	public void setTermEnumsList(List<String> termEnumsList) {
		this._termEnumsList = termEnumsList;
	}


	public Boolean getTermIsMultiEnum() {
		return _isMultiEnum;
	}


	public void setTermIsMultiEnum(Boolean isMultiEnum) {
		this._isMultiEnum = isMultiEnum;
	}
	
}
