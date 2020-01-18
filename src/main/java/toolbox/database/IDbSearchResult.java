package toolbox.database;


import java.util.List;

/// A subset of items resulting from a DBSearch
public interface IDbSearchResult {	
	public enum SORTING_ORDER { ASC, DESC };
	public List<IDbItem> getItems();
	public Long getTotalHits();
	public Integer getFromIdx();
	public Boolean getIsSuccessful();
	
}
