package metaindex.data.perspective;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class PerspectiveSection {

	public enum SECTION_TYPE {table,mozaic};
	public enum SECTION_ALIGN {left,right,center};
	private String _title;
	private List<PerspectiveField> _fields = new ArrayList<>();
	private SECTION_TYPE _type;
	private SECTION_ALIGN _align;
	
	public String getTitle() {
		return _title;
	}
	public void setTitle(String _title) {
		this._title = _title;
	}
	public List<PerspectiveField> getFields() {
		return _fields;
	}
	public void setFields(List<PerspectiveField> fields) {
		this._fields = fields;
	}
	
	public void populateFromJson(JSONObject json) {
		
		// title
		this.setTitle(json.getString("title"));
		
		// type
		this.setType(SECTION_TYPE.valueOf(json.getString("type")));
		
		// align
		this.setAlign(SECTION_ALIGN.valueOf(json.getString("align")));
				
		// fields
		JSONArray fields =  (JSONArray) json.getJSONArray("fields");
		for (Object fieldObj : fields) {
			JSONObject fieldJson = (JSONObject) fieldObj;
			PerspectiveField field = new PerspectiveField();
			field.populateFromJson(fieldJson);
			_fields.add(field);
		}
	}
	public SECTION_TYPE getType() {
		return _type;
	}
	public void setType(SECTION_TYPE type) {
		this._type = type;
	}
	public SECTION_ALIGN getAlign() {
		return _align;
	}
	public void setAlign(SECTION_ALIGN _align) {
		this._align = _align;
	}
}
