package metaindex.data.filter;

import toolbox.utils.IIdentifiable;

public interface IFilter extends IIdentifiable<Integer> {

	public final Integer ALL_ITEMS_CATALOG_ID = 0;
	public String getQuery(); 
	public void setQuery(String query);
	public void setName(String name);
	public void setId(Integer id);
}
