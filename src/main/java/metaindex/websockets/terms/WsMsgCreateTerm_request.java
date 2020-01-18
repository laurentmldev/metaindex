package metaindex.websockets.terms;

import java.util.HashMap;
import java.util.Map;

import metaindex.data.term.ICatalogTerm.TERM_DATATYPE;

public class WsMsgCreateTerm_request extends WsMsgDeleteTerm_request {

	private TERM_DATATYPE _termDatatype;
	private Map<String,String> _complementaryInfoMap=new HashMap<>();
	
	public TERM_DATATYPE getTermDatatype() {
		return _termDatatype;
	}

	public void setTermDatatype(TERM_DATATYPE termDatatype) {
		this._termDatatype = termDatatype;
	}

	public Map<String,String> getComplementaryInfoMap() {
		return _complementaryInfoMap;
	}

	public void setComplementaryInfoMap(Map<String,String> complementaryInfoMap) {
		this._complementaryInfoMap = complementaryInfoMap;
	}
}
