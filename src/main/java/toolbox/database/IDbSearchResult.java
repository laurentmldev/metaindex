package toolbox.database;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.List;

/// A subset of items resulting from a DBSearch
public interface IDbSearchResult {	
	public enum SORTING_ORDER { ASC, DESC };
	public List<IDbItem> getItems();
	public Long getTotalHits();
	public Integer getFromIdx();
	public Boolean getIsSuccessful();
	
}
