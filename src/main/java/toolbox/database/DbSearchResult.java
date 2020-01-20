package toolbox.database;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.List;
import toolbox.exceptions.DataProcessException;


/// A subset of items resulting from a DBSearch
public class DbSearchResult implements IDbSearchResult {

	/// the total items mathing corresponding search
	private Long _totalHits;
	/// starting index of items found in this DBSearchResult
	private Integer _fromIdx;
	/// The items themselves
	private List<IDbItem> _items = new ArrayList<IDbItem>();
	
	/// False if the search could not be processed successfully
	private Boolean _isSuccessful = true;
	
	public DbSearchResult() throws DataProcessException {		

	}
	
	public void addItem(IDbItem itemContents) {
		_items.add(itemContents);
	}

	@Override
	public List<IDbItem> getItems() {
		return _items;
	}
	@Override
	public Long getTotalHits() {
		return _totalHits;
	}

	public void setTotalHits(Long totalHits) {
		this._totalHits = totalHits;
	}
	@Override
	public Integer getFromIdx() {
		return _fromIdx;
	}

	public void setFromIdx(Integer fromIdx) {
		this._fromIdx = fromIdx;
	}
	
	@Override
	public Boolean getIsSuccessful() {
		return _isSuccessful;
	}

	public void setIsSuccessful(Boolean isSuccessful) {
		this._isSuccessful = isSuccessful;
	}
	
	
	
	
}
