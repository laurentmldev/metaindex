package metaindex.data.catalog;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
		if (_fields.get("_id")==null) { return ""; }
		return (String) _fields.get("_id");
	}
	/// if 'id' is empty, then remove the '_id' param from the fields list
	public void setId(String id) {
		// '_id' is the key entry-key in ElasticSearch and shall be removed from the map if left empty
		// so that ES create one
		if (id.equals("")) { _fields.remove("_id"); }
		else { _fields.put("_id", id); }
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
		if (_fields.containsKey("_id") && _fields.get("_id").equals("")) {
			_fields.remove("_id");
		}
	}
};
