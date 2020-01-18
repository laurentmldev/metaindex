package metaindex.websockets.items;

import java.util.ArrayList;
import java.util.List;

public class WsMsgDeleteItemsByQuery_request  {
		
	private String _query;
	
	// filters within the query shall be applied
	private List<String> _filtersNames = new ArrayList<>();
	
	public WsMsgDeleteItemsByQuery_request() {
	
	}
	
	public String getQuery() {
		return _query;
	}
	public void setQuery(String query) {
		this._query = query;
	}
	public List<String> getFiltersNames() {
		return _filtersNames;
	}
	public void setFiltersNames(List<String> filtersNames) {
		this._filtersNames = filtersNames;
	}
	

}
