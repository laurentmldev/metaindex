package metaindex.data.perspective;

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
