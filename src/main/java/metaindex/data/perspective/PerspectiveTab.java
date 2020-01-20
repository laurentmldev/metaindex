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

public class PerspectiveTab {

	private String _title;
	private List<PerspectiveSection> _sections = new ArrayList<>();
	
	public String getTitle() {
		return _title;
	}
	public void setTitle(String _title) {
		this._title = _title;
	}
	public List<PerspectiveSection> getSections() {
		return _sections;
	}
	public void setSections(List<PerspectiveSection> _sections) {
		this._sections = _sections;
	}
	
	public void populateFromJson(JSONObject json) {
		
		// title
		this.setTitle(json.getString("title"));
		
		// sections
		JSONArray sections =  (JSONArray) json.getJSONArray("sections");
		for (Object sectionObj : sections) {
			JSONObject sectionJson = (JSONObject) sectionObj;
			PerspectiveSection section = new PerspectiveSection();
			section.populateFromJson(sectionJson);
			_sections.add(section);
		}
	}
	
}
