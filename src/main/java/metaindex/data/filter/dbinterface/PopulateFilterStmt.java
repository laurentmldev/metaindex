package metaindex.data.filter.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import metaindex.data.filter.Filter;
import metaindex.data.filter.IFilter;
import metaindex.data.catalog.ICatalog;
import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLPopulateStmt;
import toolbox.database.sql.SQLReadStmt;
import toolbox.exceptions.DataProcessException;

class PopulateFilterStmt extends SQLPopulateStmt<IFilter>   {

	public static final String SQL_REQUEST = 
			"select filters.filter_id,filters.catalog_id,filters.name,filters.query"							
			+" from filters";

	List<ICatalog> _catalogs;
	List<IFilter> _data = new ArrayList<>();
	
	public PopulateFilterStmt(ICatalog c, List<IFilter> d, SQLDataSource ds) throws DataProcessException { 
		super(ds);
		_catalogs=new ArrayList<ICatalog>();
		_catalogs.add(c);
		_data=d;
	}
	public PopulateFilterStmt(List<ICatalog> c, SQLDataSource ds) throws DataProcessException { 
		super(ds);
		_catalogs=c;
	}
	@Override
	public IFilter mapRow(ResultSet rs, int rowNum) throws SQLException {
		IFilter d;
		ICatalog c;
		
		Integer dbKey = rs.getInt(1);
		Integer commnityDbKey = rs.getInt(2);
		
		c = _catalogs.stream()
				.filter(catalog -> catalog.getId().equals(commnityDbKey))
				.findFirst()
				.orElse(null);
		
		d = _data.stream()
				.filter(p -> p.getId().equals(dbKey))
				.findFirst()
				.orElse(null);
		
		if (d==null) { 
			d = new Filter();
			_data.add(d);
		}

		d.setId(rs.getInt(1));
		d.setName(rs.getString(3));
		d.setQuery(rs.getString(4));
		
		try { c.addFilter(d); } 
		catch (DataProcessException e) { e.printStackTrace();}
		
		return d;
	}

	@Override
	public String buildSqlQuery()  {				
		String sql = SQL_REQUEST;
		if (_catalogs.size()>0) 
		{ 
			// filter catalogs
			sql+=" where (";
			Boolean firstCatalog=true;
			for (ICatalog c : _catalogs) {
				if (!firstCatalog) { sql+= " or "; }
				sql += "filters.catalog_id='"+c.getId()+"'";
				firstCatalog=false;
			}
			sql+=" )";
		}
		
		// add specific filters
		if (_data.size()>0) {
			if (_catalogs.size()==0) { sql+=" where "; }
			else { sql+=" and "; } 
			sql+=" (";
			Boolean firstOne=true;
			for (IFilter c : _data) {
				if (!firstOne) { sql+=" or "; }
				sql += "filters.filter_id='"+c.getId()+"'";
				firstOne=false;
			}
			sql+=" )";
		}
		return sql;
	}	
					
};
