package metaindex.data.perspective.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalog;
import metaindex.data.perspective.CatalogPerspective;
import metaindex.data.perspective.ICatalogPerspective;

import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLPopulateStmt;
import toolbox.database.sql.SQLReadStreamStmt;
import toolbox.exceptions.DataProcessException;

/**
 * Load perspectives from SQL DB and update catalogs perspectives list
 * @author laurentml
 *
 */
public class PopulateCatalogPerspectiveFromDbStmt extends SQLPopulateStmt<ICatalogPerspective>   {

	private Log log = LogFactory.getLog(PopulateCatalogPerspectiveFromDbStmt.class);
	
	public static final String SQL_REQUEST = 
			"select catalog_perspective_id,catalog_id,name,perspective_json_string "							
			+"from catalog_perspectives";
	private List<ICatalog> _catalogs;
	private List<ICatalogPerspective> _data = new ArrayList<>();
	
	public PopulateCatalogPerspectiveFromDbStmt(List<ICatalog> c, List<ICatalogPerspective> d, SQLDataSource ds) throws DataProcessException { 
		super(ds);
		_catalogs=c;
		_data=d;
	}
	public PopulateCatalogPerspectiveFromDbStmt(List<ICatalog> c, SQLDataSource ds) throws DataProcessException { 
		super(ds);
		_catalogs=c;
		
	}

	@Override
	public void execute() throws DataProcessException {
		
		super.execute();
		
		// update catalogs perspectives lists
		for (ICatalog c :  _catalogs) {
			List<ICatalogPerspective> commPerspectives = _data.stream()
					.filter(t -> t.getCatalogId().equals(c.getId())).collect(Collectors.toList());
			c.updateCatalogPerspectives(commPerspectives);			
		}
		
		
	}
	@Override
	public ICatalogPerspective mapRow(ResultSet rs, int rowNum) throws SQLException {
		ICatalogPerspective d;
		Integer dbKey = rs.getInt(1);
		 
		d = _data.stream()
			.filter(p -> p.getId().equals(dbKey))
			.findFirst()
			.orElse(null);
		if (d==null) {
			d = new CatalogPerspective(); 
			_data.add(d);
		}
		d.setId(rs.getInt(1));
		d.setCatalogId(rs.getInt(2));
		d.setName(rs.getString(3));
		d.setDefinition(rs.getString(4));
		
		try { 
			JSONObject json = new JSONObject(d.getDefinition());
			d.populateFromJson(json); 
		} 
		catch (Exception e) {
			throw new SQLException("While loading perspective '"+d.getName()+"', could not parse json definition : "+e.getMessage());
			//e.printStackTrace();
			
		}		
		return d;
	}

	@Override
	public String buildSqlQuery()  {				
		String sql = SQL_REQUEST;
		sql+=" where (";
		Boolean firstOne=true;
		for (ICatalog c : _catalogs) {
			if (!firstOne) { sql+=" or "; }
			sql+="catalog_id="+c.getId();	
			firstOne=false;
		}
		sql+=")";
		if (_data.size()>0) {
			sql+=" and (";		
			Iterator<ICatalogPerspective> it = _data.iterator();
			firstOne=true;
			while (it.hasNext()) {
				if (!firstOne) { sql+=" or "; }
				sql += "catalog_perspective_id='"+it.next().getId()+"'";
				firstOne=false;
			}
			sql+=")";
		}
		return sql;
	}
					
};
