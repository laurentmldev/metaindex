package metaindex.data.catalog;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import toolbox.database.IDbItem;

public class UserItemContents implements IDbItem {
	
	private String _name;
	private String _thumbnailUrl;
	private Date _lastModifTimestamp=new Date(0);
	private Integer _lastModifUserId;
	public Map<String,Object> _fields = new HashMap<String,Object>();
	
	
	@Override
	public String getName() {
		return _name;
	}

	@Override
	public String getId() {
		if (_fields.get(DB_ID_FIELD_NAME)==null) { return ""; }
		return (String) _fields.get(DB_ID_FIELD_NAME);
	}
	/// if 'id' is empty, then remove the '_id' param from the fields list
	public void setId(String id) {
		// '_id' is the key entry-key in ElasticSearch and shall be removed from the map if left empty
		// so that ES create one
		if (id.equals("")) { _fields.remove(DB_ID_FIELD_NAME); }
		else { _fields.put(DB_ID_FIELD_NAME, id); }
	}
	
	@Override
	public Date getLastModifTimestamp() {
		return _lastModifTimestamp;
	}
	public void setLastModifTimestamp(Date d) {
		_lastModifTimestamp=d;
	}
	
	@Override
	public Integer getLastModifUserId() {
		return _lastModifUserId;
	}
	public void getLastModifUserId(Integer uid) {
		_lastModifUserId=uid;
	}
	@Override
	public String getThumbnailUrl() {
		return _thumbnailUrl;
	}

	@Override
	public Map<String, Object> getData() {
		return _fields;
	}
	public void setData(Map<String, Object> fields) {
		_fields.putAll(fields);
		
		// '_id' is the entry-key in ElasticSearch and shall be removed from the map if left empty
		// so that ES create one
		if (_fields.containsKey(DB_ID_FIELD_NAME) && _fields.get(DB_ID_FIELD_NAME).equals("")) {
			_fields.remove(DB_ID_FIELD_NAME);
		}
	}

	@Override
	public Object getValue(String fieldName) {
		return _fields.get(fieldName);
	}

	@Override
	public void setValue(String fieldName, Object value) {
		_fields.put(fieldName, value);		
	}

	@Override
	public Set<String> getFieldsList() {
		return _fields.keySet();
	}
};
