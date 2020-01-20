package metaindex.data.filter;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class Filter implements IFilter {

	private String _query;
	private String _name;
	private Integer _id;
	
	@Override
	public String getQuery() { return _query; }
	public void setQuery(String query) { _query=query; }
	
	@Override
	public String getName() { return _name; }
	public void setName(String name) { _name=name; }
	@Override
	public Integer getId() { return _id; } 
	public void setId(Integer id) { _id=id; }
}
