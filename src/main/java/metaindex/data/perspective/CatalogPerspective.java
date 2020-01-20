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

import toolbox.utils.IIdentifiable;

public class CatalogPerspective implements ICatalogPerspective, IIdentifiable<Integer> {

	private String _name;
	private Integer _id;
	private Integer _catalogId;
	private String _definition;
	private List<PerspectiveTab> _tabs = new ArrayList<>();
	
	
	@Override
	public String getName() {
		return _name;
	}

	@Override
	public Integer getId() {		
		return _id;
	}

	@Override
	public Integer getCatalogId() {		
		return _catalogId;
	}

	@Override
	public void setCatalogId(Integer id) {
		_catalogId=id;		
	}

	@Override
	public void setName(String name) {
		_name=name;		
	}

	@Override
	public void setId(Integer id) {
		_id=id;		
	}

	@Override
	public String getDefinition() {		
		return _definition;
	}

	@Override
	public void setDefinition(String def) {
		_definition=def;		
	}

	@Override
	public void populateFromJson(JSONObject json) {
		
		// tabs
		JSONArray tabs =  (JSONArray) json.getJSONArray("tabs");
		for (Object tabObj : tabs) {
			JSONObject tabJson = (JSONObject) tabObj;
			PerspectiveTab tab = new PerspectiveTab();
			tab.populateFromJson(tabJson);
			_tabs.add(tab);
		}
	}
	@Override
	public List<PerspectiveTab> getTabs() {
		return _tabs;
	}
	@Override
	public void setTabs(List<PerspectiveTab> tabs) {
		_tabs=tabs;
	}
			

}
