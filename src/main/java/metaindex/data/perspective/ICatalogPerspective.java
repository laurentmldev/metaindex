package metaindex.data.perspective;

import toolbox.utils.IIdentifiable;

import java.util.List;

import org.json.JSONObject;

/**
 * A perspective is a custom way to represent contents of an item
 * It is represented as a JSON tree made like following example :
 * { tabs: [
 *		tab_1: {
 *			title:"my tab",
 *			sections: [
 *				   section_1 : {
 *					   title : "my section",
 *					   fields: [ 
 *								nom : {
 *									size:"small",
 *									color:"normal"
 *								}
 *							]
 *				   }
 *				]
 *      }
 *  ]} 
 * @author laurentml
 *
 * Validity of a given description is not ensured by DB structure,
 * in order to minimize database schema complexity.
 * This is judged acceptable since this data is built and read by the javascript code, not by user.
 * @see catalogs/details_perspectives.jsp and items/details_perspectives.jsp
 */
public interface ICatalogPerspective extends IIdentifiable<Integer>{
								 
	Integer getCatalogId();
	void setCatalogId(Integer id);	
	void setName(String name);
	void setId(Integer id);	
	String getDefinition();	
	void setDefinition(String def);
	public void populateFromJson(JSONObject json);
	public List<PerspectiveTab> getTabs();
	public void setTabs(List<PerspectiveTab> tabs);

}
