package metaindex.data.perspective;

import org.json.JSONObject;

public class PerspectiveField {

	public static enum FIELD_SIZE { small, medium, big };
	public static enum FIELD_WEIGHT { normal, bold, italic };
	public static enum FIELD_COLOR { normal, black, red, yellow, green, orange, blue, purple };
	private FIELD_SIZE _size = FIELD_SIZE.medium;
	private String _term;
	private Boolean _showTitle=true;
	private FIELD_COLOR _color=FIELD_COLOR.normal;
	private FIELD_WEIGHT _weight = FIELD_WEIGHT.normal;
	
	public FIELD_SIZE getSize() {
		return _size;
	}
	public void setSize(FIELD_SIZE _size) {
		this._size = _size;
	}
	
	public void populateFromJson(JSONObject json) {		
		this.setTerm(json.getString("term"));
		this.setSize(FIELD_SIZE.valueOf(json.getString("size")));
		this.setColor(FIELD_COLOR.valueOf(json.getString("color")));
		this.setShowTitle(json.getBoolean("showTitle"));
		this.setWeight(FIELD_WEIGHT.valueOf(json.getString("weight")));
		
	}
	public String getTerm() {
		return _term;
	}
	public void setTerm(String _term) {
		this._term = _term;
	}
	public Boolean getShowTitle() {
		return _showTitle;
	}
	public void setShowTitle(Boolean showTitle) {
		this._showTitle = showTitle;
	}
	public FIELD_COLOR getColor() {
		return _color;
	}
	public void setColor(FIELD_COLOR color) {
		this._color = color;
	}
	public FIELD_WEIGHT getWeight() {
		return _weight;
	}
	public void setWeight(FIELD_WEIGHT weight) {
		this._weight = weight;
	}
	
}
