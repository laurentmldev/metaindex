package metaindex.data.filter;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.utils.IIdentifiable;

public interface IFilter extends IIdentifiable<Integer> {

	public String getQuery(); 
	public void setQuery(String query);
	public void setName(String name);
	public void setId(Integer id);
	public Boolean getIsBuiltin();
}
