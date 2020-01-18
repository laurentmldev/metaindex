package metaindex.websockets.terms;

public class WsMsgDeleteTerm_request  {
		
	private Integer _catalogId;
	private String _termName;
	
	public String getTermName() {
		return _termName;
	}

	public void setTermName(String termName) {
		this._termName = termName;
	}

	public Integer getCatalogId() {
		return _catalogId;
	}

	public void setCatalogId(Integer catalogId) {
		this._catalogId = catalogId;
	}

}
